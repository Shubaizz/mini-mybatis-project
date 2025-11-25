package com.shubai.mybatis.datasource.pooled;

import com.shubai.mybatis.datasource.unpooled.UnpooledDataSource;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;

/**
 * ClassName: PooledDataSource
 * Description: 池化数据源
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 13:51
 * Version: 1.0
 */
public class PooledDataSource implements DataSource {

    /**
     * 日志模块
     */
    private org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDataSource.class);

    /**
     * 连接池状态
     */
    private final PoolState state = new PoolState(this);

    /**
     * 数据源(非池化)
     */
    private final UnpooledDataSource dataSource;

    /**
     * 连接池中允许的最大活动（正在使用的）连接数，超过这个数后新的请求会被阻塞等待
     */
    protected int poolMaximumActiveConnections = 10;

    /**
     * 连接池中允许的最大空闲（未被使用的）连接数，超过这个数时多余的空闲连接会被关闭
     */
    protected int poolMaximumIdleConnections = 5;

    /**
     * 单个连接被占用的最长时间（毫秒），超过这个时间会被强制回收
     */
    protected int poolMaximumCheckoutTime = 20000;

    /**
     * 没有可用连接时，获取连接的最大等待时间（毫秒），超时后会抛出异常
     */
    protected int poolTimeToWait = 20000;

    /**
     * 用于检测连接是否可用的 SQL 语句（ping 查询），默认未设置
     */
    protected String poolPingQuery = "NO PING QUERY SET";

    /**
     * 是否启用 ping 查询来检测连接可用性
     */
    protected boolean poolPingEnabled = false;

    /**
     * 连接多久未被使用后，再次使用时会触发 ping 检查（毫秒）
     */
    protected int poolPingConnectionsNotUsedFor = 0;

    /**
     * 用于标识当前连接类型的哈希码（根据url、用户名、密码生成），用于区分不同配置的连接
     */
    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }

    /**
     * 将连接归还到连接池的方法。
     * 如果连接有效且池中空闲连接未满，则放回空闲池，否则关闭连接。
     *
     * @param connection 要归还的连接
     * @throws SQLException 如果归还过程中发生数据库错误，抛出异常
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        // 保证线程安全，防止多线程同时操作连接池状态
        synchronized (state){
            // 从活跃连接列表中移除该连接（表示不再被使用）
            state.activeConnections.remove(connection);
            // 检查连接是否有效
            if (connection.isValid()){
                // 如果空闲连接池未满，且连接类型匹配（防止不同配置的连接混用）
                if(state.idleConnections.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode){
                    // 累加该连接的使用时长
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    // 如果连接不是自动提交，回滚未提交的事务，保证连接干净
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    // 用真实连接新建一个PooledConnection对象（避免代理对象复用带来问题）
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    // 放入空闲连接池
                    state.idleConnections.add(newConnection);
                    // 继承原连接的创建和最后使用时间
                    newConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    // 使原连接失效（防止被再次使用）
                    connection.invalidate();
                    // 记录日志
                    logger.info("Returned connection " + newConnection.getRealHashCode() + " to pool.");
                    // 通知等待连接的线程有新连接可用
                    state.notifyAll();
                }else {
                    // 空闲池已满或连接类型不匹配，直接关闭连接
                    // 累加该连接的使用时长
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    // 回滚未提交的事务
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    // 关闭真实数据库连接
                    connection.getRealConnection().close();
                    // 记录日志
                    logger.info("Closed connection " + connection.getRealHashCode() + ".");
                    // 使连接失效
                    connection.invalidate();
                }
            }else{
                // 连接无效，丢弃并计数
                logger.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
            }
        }
    }

    /**
     * 获取一个可用的数据库连接（PooledConnection），如果没有可用连接则等待或创建新连接
     *
     * @param username 数据库用户名
     * @param password 数据库密码
     * @return 一个有效的PooledConnection对象
     * @throws SQLException 如果无法获取连接，抛出异常
     */
    private PooledConnection popConnection(String username, String password) throws SQLException {
        // 标记本次等待是否已计数
        boolean countedWait = false;
        // 最终要返回的连接
        PooledConnection conn = null;
        // 记录开始获取连接的时间
        long t = System.currentTimeMillis();
        // 本地坏连接计数
        int localBadConnectionCount = 0;
        // 循环直到获取到可用连接
        while (conn == null) {
            // 保证线程安全，锁住连接池状态
            synchronized (state) {
                // 如果有空闲连接，从空闲连接池取出第一个连接
                if (!state.idleConnections.isEmpty()) {
                    conn = state.idleConnections.remove(0);
                    logger.info("Checked out connection " + conn.getRealHashCode() + " from pool.");
                } else {
                    // 没有空闲连接
                    // 活跃连接数未达上限，可以创建新连接
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        logger.info("Created connection " + conn.getRealHashCode() + ".");
                    } else {
                        // 活跃连接数已达上限，不能新建连接
                        // 获取最早被借出的连接
                        PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                        // 计算该连接已被占用的时长
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        // 如果该连接被占用超时，可以强制回收
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            // 超时回收次数+1
                            state.claimedOverdueConnectionCount++;
                            // 累加超时连接的占用时长
                            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            // 累加总占用时长
                            state.accumulatedCheckoutTime += longestCheckoutTime;
                            // 从活跃连接池移除
                            state.activeConnections.remove(oldestActiveConnection);
                            // 如果连接不是自动提交，回滚未提交的事务，保证连接干净
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                oldestActiveConnection.getRealConnection().rollback();
                            }
                            // 用原连接的真实连接对象新建一个PooledConnection
                            conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            // 继承原连接的创建和最后使用时间
                            conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                            conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                            // 使原连接失效
                            oldestActiveConnection.invalidate();
                            // 记录日志
                            logger.info("Claimed overdue connection " + conn.getRealHashCode() + ".");
                        } else {
                            // 没有超时连接，只能等待
                            try {
                                if (!countedWait) {
                                    // 等待次数+1
                                    state.hadToWaitCount++;
                                    // 标记已计数
                                    countedWait = true;
                                }
                                logger.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                // 记录等待开始时间
                                long wt = System.currentTimeMillis();
                                // 等待指定时间
                                state.wait(poolTimeToWait);
                                // 累加等待时长
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                // 如果线程被中断，跳出循环
                                break;
                            }
                        }

                    }
                }
                // 成功获得到连接
                if (conn != null) {
                    // 检查连接是否有效
                    if (conn.isValid()) {
                        // 如果不是自动提交，回滚未提交事务，保证连接干净
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        // 设置连接类型码（用于区分不同配置的连接）
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        // 设置借出时间和最后使用时间
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        // 加入活跃连接池
                        state.activeConnections.add(conn);
                        // 请求次数+1
                        state.requestCount++;
                        // 累加本次请求耗时
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        // 连接无效，尝试重新获取
                        logger.info("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        // 坏连接计数+1
                        state.badConnectionCount++;
                        // 本地坏连接计数+1
                        localBadConnectionCount++;
                        conn = null;
                        // 如果坏连接次数超过容忍上限，抛出异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            logger.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        // 如果最终没有获取到连接，抛出严重异常
        if (conn == null) {
            logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }
        // 返回可用连接
        return conn;
    }

    /**
     * 强制关闭连接池中所有连接的方法。
     * 关闭连接池中所有活跃和空闲的数据库连接。
     */
    public void forceCloseAll() {
        // 保证线程安全，防止多线程同时操作连接池状态
        synchronized (state) {
            // 重新计算当前连接类型的哈希码（根据url、用户名、密码）
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            // 关闭所有活跃（正在使用中的）连接
            for (int i = state.activeConnections.size(); i > 0; i--) {
                try {
                    // 从活跃连接列表中移除最后一个连接
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    // 将连接标记为失效，防止被再次使用
                    conn.invalidate();
                    // 获取真实的数据库连接对象
                    Connection realConn = conn.getRealConnection();
                    // 如果不是自动提交，回滚未提交的事务，保证数据一致性
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    // 关闭真实的数据库连接，释放资源
                    realConn.close();
                } catch (Exception ignore) {
                    // 捕获异常但不处理，忽略关闭过程中的错误
                }
            }
            // 关闭所有空闲（未被使用的）连接
            for (int i = state.idleConnections.size(); i > 0; i--) {
                try {
                    // 从空闲连接列表中移除最后一个连接
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    // 将连接标记为失效
                    conn.invalidate();
                    // 获取真实的数据库连接对象
                    Connection realConn = conn.getRealConnection();
                    // 如果不是自动提交，回滚未提交的事务
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    // 关闭真实的数据库连接
                    realConn.close();
                } catch (Exception ignore) {
                    // 捕获异常但不处理，忽略关闭过程中的错误
                }
            }
            logger.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    /**
     * 检查一个连接是否仍然可用的方法。
     *
     * @param conn 需要检测的连接（PooledConnection 类型）
     * @return 如果连接可用返回 true，否则返回 false
     */
    protected boolean pingConnection(PooledConnection conn) {
        // 默认认为连接是可用的
        boolean result = true;
        try {
            // 检查真实数据库连接是否已关闭
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            // 如果检查过程中抛出异常，说明连接不可用
            logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }
        // 如果连接未关闭，并且启用了 ping 检测，且设置了检测的时间阈值，并且该连接距离上次使用已经超过设定的时间
        if (result && poolPingEnabled && poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
            try {
                // 打印调试日志，说明正在测试该连接
                logger.info("Testing connection " + conn.getRealHashCode() + " ...");
                // 获取真实的数据库连接
                Connection realConn = conn.getRealConnection();
                // 创建一个 SQL 语句对象，并执行 ping 查询（如 select 1），用于检测连接是否真的可用
                Statement statement = realConn.createStatement();
                ResultSet resultSet = statement.executeQuery(poolPingQuery);
                resultSet.close();
                // 如果不是自动提交，回滚未提交的事务，保证连接干净
                if (!realConn.getAutoCommit()) {
                    realConn.rollback();
                }
                // 执行到这里说明连接可用
                result = true;
                logger.info("Connection " + conn.getRealHashCode() + " is GOOD!");
            } catch (Exception e) {
                // 如果 ping 查询执行失败，说明连接不可用
                logger.info("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                try {
                    // 关闭真实数据库连接，释放资源
                    conn.getRealConnection().close();
                } catch (SQLException ignore) {
                    // 忽略关闭异常
                }
                result = false;
                logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            }
        }
        // 返回最终检测结果
        return result;
    }

    /**
     * 解包池化连接，获取其“真实”的数据库连接对象。
     *
     * @param conn 需要解包的连接（可能是被代理的池化连接）
     * @return 真实的数据库连接对象
     */
    public static Connection unwrapConnection(Connection conn) {
        // 判断传入的连接对象是否是一个由JDK动态代理生成的代理类
        if (Proxy.isProxyClass(conn.getClass())) {
            // 获取该代理对象背后的InvocationHandler
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            // 判断这个InvocationHandler是否是PooledConnection类型
            if (handler instanceof PooledConnection) {
                // 如果是，则返回其内部持有的真实数据库连接
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        // 如果不是代理类，或者不是PooledConnection类型，直接返回原始连接
        return conn;
    }

    /**
     * 组装连接类型的哈希码，用于唯一标识当前连接配置（url、用户名、密码）。
     * 这样可以区分不同数据库配置的连接池，防止混用。
     *
     * @param url      数据库连接URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @return 连接类型的哈希码
     */
    private int assembleConnectionTypeCode(String url, String username, String password) {
        // 将url、用户名、密码拼接成一个字符串，然后计算其hashCode作为唯一标识
        return ("" + url + username + password).hashCode();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }


    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
        this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
    }

    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }
}
