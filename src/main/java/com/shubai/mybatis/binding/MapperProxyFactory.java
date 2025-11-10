package com.shubai.mybatis.binding;

import com.shubai.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * ClassName: MapperProxyFactory
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 14:32
 * Version: 1.0
 */
public class MapperProxyFactory<T> {

    // 被代理的 Mapper 接口对应的 Class 对象
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        // 根据传入的 SqlSession 和 Mapper 接口创建 MapperProxy 对象，MapperProxy 实现了 InvocationHandler 接口，封装了对 Mapper 方法调用的处理逻辑
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        // 创建Mapper接口的动态代理对象
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }
}
