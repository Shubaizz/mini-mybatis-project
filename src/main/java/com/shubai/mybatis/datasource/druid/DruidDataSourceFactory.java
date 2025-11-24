package com.shubai.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.shubai.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * ClassName: DruidDataSourceFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:37
 * Version: 1.0
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    /**
     * 数据源配置属性
     */
    private Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }
}
