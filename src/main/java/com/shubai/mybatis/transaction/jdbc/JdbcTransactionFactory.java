package com.shubai.mybatis.transaction.jdbc;

import com.shubai.mybatis.session.TransactionIsolationLevel;
import com.shubai.mybatis.transaction.Transaction;
import com.shubai.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * ClassName: JdbcTransactionFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:09
 * Version: 1.0
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
