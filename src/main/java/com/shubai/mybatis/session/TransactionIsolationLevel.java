package com.shubai.mybatis.session;

import java.sql.Connection;

/**
 * ClassName: TransactionIsolationLevel
 * Description: 事务隔离级别枚举类型
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:06
 * Version: 1.0
 */
public enum TransactionIsolationLevel {

    // 不使用事务隔离级别
    NONE(Connection.TRANSACTION_NONE),
    // 只允许读取已提交的数据，防止脏读
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    // 允许读取未提交的数据，可能会出现脏读
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    // 可重复读，防止不可重复读和脏读
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    // 串行化，最高级别，防止所有并发问题
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
