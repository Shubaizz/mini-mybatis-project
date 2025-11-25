package com.shubai.mybatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: PoolState
 * Description: 池状态
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 14:40
 * Version: 1.0
 */
public class PoolState {

    /**
     * 关联的数据源对象，表示当前连接池属于哪个数据源
     */
    protected PooledDataSource dataSource;

    /**
     * 空闲连接列表，存放当前未被使用的数据库连接
     */
    protected final List<PooledConnection> idleConnections = new ArrayList<>();

    /**
     * 活跃连接列表，存放当前正在被使用的数据库连接
     */
    protected final List<PooledConnection> activeConnections = new ArrayList<>();

    /**
     * 请求数据库连接的总次数，用于统计
     */
    protected long requestCount = 0;

    /**
     * 所有请求连接所花费的总时间（毫秒），用于统计平均请求时间
     */
    protected long accumulatedRequestTime = 0;

    /**
     * 所有连接被使用（签出）的总时间（毫秒），用于统计平均使用时长
     */
    protected long accumulatedCheckoutTime = 0;

    /**
     * 被强制回收（超时未归还）的连接次数
     */
    protected long claimedOverdueConnectionCount = 0;

    /**
     * 被强制回收的连接累计超时时间（毫秒）
     */
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;

    /**
     * 申请连接时等待的总时间（毫秒）
     */
    protected long accumulatedWaitTime = 0;

    /**
     * 申请连接时需要等待的次数
     */
    protected long hadToWaitCount = 0;

    /**
     * 获取到无效（坏）连接的次数
     */
    protected long badConnectionCount = 0;

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    public synchronized long getAverageRequestTime() {
        return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    }

    public synchronized long getAverageWaitTime() {
        return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    }

    public synchronized long getHadToWaitCount() {
        return hadToWaitCount;
    }

    public synchronized long getBadConnectionCount() {
        return badConnectionCount;
    }

    public synchronized long getClaimedOverdueConnectionCount() {
        return claimedOverdueConnectionCount;
    }

    public synchronized long getAverageOverdueCheckoutTime() {
        return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
    }

    public synchronized long getAverageCheckoutTime() {
        return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
    }

    public synchronized int getIdleConnectionCount() {
        return idleConnections.size();
    }

    public synchronized int getActiveConnectionCount() {
        return activeConnections.size();
    }
}
