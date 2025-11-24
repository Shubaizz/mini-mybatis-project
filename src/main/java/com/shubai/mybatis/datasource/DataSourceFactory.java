package com.shubai.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * ClassName: DataSourceFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:37
 * Version: 1.0
 */
public interface DataSourceFactory {

    /**
     * 设置数据源属性
     */
    void setProperties(Properties props);

    /**
     * 获取数据源
     */
    DataSource getDataSource();
}
