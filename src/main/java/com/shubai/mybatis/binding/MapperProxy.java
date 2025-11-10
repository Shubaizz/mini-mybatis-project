package com.shubai.mybatis.binding;

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

    // TODO:实际上这里应该是 SqlSession 类型，这里简化为 Map<String, String>，用于存储 Sql 语句，Key 是 "namespace.id"，Value 是 SQL 语句
    private Map<String,String> sqlSession;

    // 被代理的Mapper接口对应的Class对象
    private final Class<T> mapperInterface;

    public MapperProxy(Map<String,String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO:特殊处理，当调用 Object 的方法时，直接调用当前代理对象的方法
        if (Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this, args);
        } else {
            return "MapperProxy:" + sqlSession.get(mapperInterface.getName()+ "." + method.getName());
        }
    }

    private static final long serialVersionUID = 1L;
}
