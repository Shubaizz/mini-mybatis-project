package com.shubai.mybatis.datasource.pooled;

import com.shubai.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * ClassName: PooledDataSourceFactory
 * Description: 池化数据源工厂
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 14:42
 * Version: 1.0
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(props.getProperty("driver"));
        pooledDataSource.setUrl(props.getProperty("url"));
        pooledDataSource.setUsername(props.getProperty("username"));
        pooledDataSource.setPassword(props.getProperty("password"));
        return pooledDataSource;
    }
}
