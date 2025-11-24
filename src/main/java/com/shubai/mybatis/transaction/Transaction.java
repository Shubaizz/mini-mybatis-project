package com.shubai.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: Transaction
 * Description: 数据库事务接口
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:05
 * Version: 1.0
 */
public interface Transaction {
    /**
     * 获取当前事务对应的数据库连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     */
    void rollback() throws SQLException;

    /**
     * 关闭连接，释放资源
     */
    void close() throws SQLException;

}
