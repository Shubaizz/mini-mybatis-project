package com.shubai.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: MapperRegistry
 * Description: Mapper注册中心
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 15:43
 * Version: 1.0
 */
public class MapperRegistry {

    /**
     * 全局配置对象
     */
    private Configuration config;

    /**
     * 将已添加的映射器代理加入到 HashMap
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.config = configuration;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null){
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e){
            throw new RuntimeException("Error getting mapper instance. Cause: " + e);
        }
    }

    public <T> void addMapper(Class<T> type) {
        // 只有 Mapper 是接口类型才能够被添加
        if (type.isInterface()) {
            if (hasMapper(type)) {
                // 如果重复添加了，报错
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            // 将对应的 Mapper 接口的 MapperProxyFactory 添加到 knownMappers 中
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public void addMappers(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }
}
