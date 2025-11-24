package com.shubai.mybatis.session;

import com.shubai.mybatis.binding.MapperRegistry;
import com.shubai.mybatis.datasource.druid.DruidDataSourceFactory;
import com.shubai.mybatis.mapping.Environment;
import com.shubai.mybatis.mapping.MappedStatement;
import com.shubai.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.shubai.mybatis.type.TypeAliasRegistry;

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
     * 环境配置
     */
    protected Environment environment;

    /**
     * 映射器注册中心
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 存储已解析的 MappedStatement 对象
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    /**
     * 类型别名注册中心
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        // 注册事务管理器工厂的别名
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
    }

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

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
