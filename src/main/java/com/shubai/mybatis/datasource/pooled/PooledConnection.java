package com.shubai.mybatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: PooledConnection
 * Description: 池化的数据库连接，TODO:通过动态代理拦截Connection的close方法，将连接返回给连接池而不是关闭连接
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 13:48
 * Version: 1.0
 */
public class PooledConnection implements InvocationHandler {

    /**
     * 定义一个常量字符串，表示数据库连接的关闭方法名 "close"
     */
    private static final String CLOSE = "close";

    /**
     * 定义一个常量数组，里面只包含 Connection.class，后续用于动态代理
     */
    private static final Class<?>[] IFACES = new Class[]{Connection.class};

    /**
     * 保存真实数据库连接的 hashCode，用于标识和比较连接对象
     */
    private int hashCode = 0;

    /**
     * 关联的连接池数据源对象，用于管理连接的回收和复用
     */
    private PooledDataSource dataSource;

    /**
     * 真实的数据库连接对象，所有数据库操作最终都委托给它
     */
    private Connection realConnection;

    /**
     * 代理的数据库连接对象，外部实际拿到的是这个代理对象
     */
    private Connection proxyConnection;

    /**
     * 记录该连接被借出（checkout）的时间戳（毫秒）
     */
    private long checkoutTimestamp;

    /**
     * 记录该连接创建的时间戳（毫秒）
     */
    private long createdTimestamp;

    /**
     * 记录该连接上次被使用的时间戳（毫秒）
     */
    private long lastUsedTimestamp;

    /**
     * 连接类型的标识码（通常由 url、用户名、密码等信息生成）
     */
    private int connectionTypeCode;

    /**
     * 标记该连接是否有效（true 表示可用，false 表示已失效）
     */
    private boolean valid;

    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取当前被调用方法的名称
        String methodName = method.getName();
        // TODO:如果是调用 CLOSE 关闭链接方法，则将链接加入连接池中，并返回null
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            dataSource.pushConnection(this);
            return null;
        } else {
            // TODO:如果调用的方法不是 Object 类自带的方法（如 toString、hashCode 等），在方法调用之前要检查connection是否还是合法的,不合法要抛出SQLException
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            // 通过反射调用真实数据库连接的对应方法，并传递参数
            return method.invoke(realConnection, args);
        }
    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }

    public void invalidate() {
        valid = false;
    }

    public boolean isValid() {
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PooledConnection) {
            return realConnection.hashCode() == (((PooledConnection) obj).realConnection.hashCode());
        } else if (obj instanceof Connection) {
            return hashCode == obj.hashCode();
        } else {
            return false;
        }
    }
}
