package com.shubai.mybatis.session.defaults;

import com.shubai.mybatis.binding.MapperRegistry;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.session.SqlSession;
import com.shubai.mybatis.session.SqlSessionFactory;

/**
 * ClassName: DefaultSqlSessionFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 16:02
 * Version: 1.0
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    // 全局配置对象
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
