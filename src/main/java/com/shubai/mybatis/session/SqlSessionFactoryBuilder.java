package com.shubai.mybatis.session;

import com.shubai.mybatis.builder.xml.XMLConfigBuilder;
import com.shubai.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * ClassName: SqlSessionFactoryBuilder
 * Description: 构建 SqlSessionFactory 的构建器
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:50
 * Version: 1.0
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
