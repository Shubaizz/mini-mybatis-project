package com.shubai.mybatis.datasource.unpooled;

import com.shubai.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * ClassName: UnpooledDataSourceFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/25 14:15
 * Version: 1.0
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {
    /**
     * 数据源配置属性
     */
    protected Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
        unpooledDataSource.setDriver(props.getProperty("driver"));
        unpooledDataSource.setUrl(props.getProperty("url"));
        unpooledDataSource.setUsername(props.getProperty("username"));
        unpooledDataSource.setPassword(props.getProperty("password"));
        return unpooledDataSource;
    }
}
