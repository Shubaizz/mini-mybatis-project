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

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO:特殊处理，当调用 Object 的方法时，直接调用当前代理对象的方法
        if (Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this, args);
        } else {
            // 根据 Mapper 接口和方法获取 statement = namespace.id
            String statement = mapperInterface.getName() + "." + method.getName();
            System.out.println("MapperProxy - statement: " + statement);
            /**
             * TODO:
             *  因为{@link SqlSession}接口存在 select, insert, update, delete 等多种方法，这里不清楚需要调用哪一个方法。
             *  待代码完善后，之后需要statement去mapper.xml中查找属于哪一种SQL操作，然后调用对应的方法。
             *  这里简单起见，直接调用selectOne方法。
             */
            return sqlSession.selectOne(statement, args);
        }
    }

    private static final long serialVersionUID = 1L;
}
