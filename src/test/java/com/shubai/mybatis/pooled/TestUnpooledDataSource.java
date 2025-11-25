package com.shubai.mybatis.pooled;

import com.shubai.mybatis.datasource.pooled.PooledDataSource;
import com.shubai.mybatis.datasource.unpooled.UnpooledDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: TestUnpooledDataSource
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 14:18
 * Version: 1.0
 */
public class TestUnpooledDataSource {

    @Test
    public void test() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/powernode?useUnicode=true");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("952298144");
        // 持续获得链接
        while (true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            // TODO:请通过注释或不注释下面这行代码，观察连接池的行为
            // connection.close();
        }
    }

    @Test
    public void test2() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/powernode?useUnicode=true");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("952298144");
        Connection connection = pooledDataSource.getConnection();
        System.out.println(connection);
        connection.close();
        connection = pooledDataSource.getConnection();
        System.out.println(connection);
    }

    @Test
    public void test3() throws SQLException, InterruptedException {
        UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
        unpooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        unpooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/powernode?useUnicode=true");
        unpooledDataSource.setUsername("root");
        unpooledDataSource.setPassword("952298144");
        Connection connection = unpooledDataSource.getConnection();
        System.out.println(connection);
    }
}