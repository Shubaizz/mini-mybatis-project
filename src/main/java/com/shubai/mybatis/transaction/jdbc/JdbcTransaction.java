package com.shubai.mybatis.transaction.jdbc;

import com.shubai.mybatis.session.TransactionIsolationLevel;
import com.shubai.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: JdbcTransaction
 * Description: 基于 JDBC 的事务管理器，直接利用 JDBC 的 commit、rollback。依赖于数据源获得的连接来管理事务范围。
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:07
 * Version: 1.0
 */
public class JdbcTransaction implements Transaction {

    /**
     * 数据库连接
     */
    protected Connection connection;

    /**
     * 数据源
     */
    protected DataSource dataSource;

    /**
     * 事务隔离级别
     */
    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;

    /**
     * 是否自动提交
     */
    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.close();
        }
    }

    /**
     * 打开连接，并设置事务隔离级别和自动提交模式
     */
    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
    }
}
