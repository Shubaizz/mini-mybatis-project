package com.shubai.mybatis.binding;

import com.shubai.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * ClassName: MapperProxy
 * Description: TODO:Mapper代理类，用于为Mapper接口创建动态代理实例，该类实现了InvocationHandler接口，封装了对Mapper接口方法调用的处理逻辑。
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 14:26
 * Version: 1.0
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    // SqlSession对象，用于执行SQL语句
    private SqlSession sqlSession;

    // 被代理的Mapper接口对应的Class对象
    private final Class<T> mapperInterface;

    // 映射器方法的缓存
    private final Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO:特殊处理，当调用 Object 的方法时，直接调用当前代理对象的方法
        if (Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this, args);
        } else {
            final MapperMethod mapperMethod = cachedMapperMethod(method);
            return mapperMethod.execute(sqlSession, args);
        }
    }

    /**
     * 在缓存中查找对应 Mapper 接口对应方法 method 的包装方法 MapperMethod
     */
    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            // 如果缓存中没有，则新建对应映射器方法对象
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

    private static final long serialVersionUID = 1L;
}
