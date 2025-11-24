package com.shubai.mybatis.session;

import com.shubai.mybatis.binding.MapperRegistry;
import com.shubai.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: Configuration
 * Description: 全局配置类，存放核心配置文件解析出来的内容
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:23
 * Version: 1.0
 */
public class Configuration {

    /**
     * 映射器注册中心
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 存储已解析的 MappedStatement 对象
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }
}
