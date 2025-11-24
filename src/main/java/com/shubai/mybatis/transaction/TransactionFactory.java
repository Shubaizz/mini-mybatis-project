package com.shubai.mybatis.transaction;

import com.shubai.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * ClassName: TransactionFactory
 * Description: 数据库事务工厂接口
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:05
 * Version: 1.0
 */
public interface TransactionFactory {

    /**
     * 根据已有的连接创建一个 {@link Transaction} 实例。
     *
     * @param conn 已有的数据库连接
     * @return Transaction
     */
    Transaction newTransaction(Connection conn);

    /**
     * 根据数据源创建一个 {@link Transaction}。
     *
     * @param dataSource 用于获取连接的数据源
     * @param level      期望的隔离级别
     * @param autoCommit 是否自动提交
     * @return Transaction
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
