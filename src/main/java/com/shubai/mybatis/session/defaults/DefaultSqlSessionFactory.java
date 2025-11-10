package com.shubai.mybatis.session.defaults;

import com.shubai.mybatis.binding.MapperRegistry;
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

    // Mapper 注册中心，用于获取 Mapper 代理对象
    private final MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
