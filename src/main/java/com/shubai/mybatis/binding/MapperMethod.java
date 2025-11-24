package com.shubai.mybatis.binding;

import com.shubai.mybatis.mapping.MappedStatement;
import com.shubai.mybatis.mapping.SqlCommandType;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * ClassName: MapperMethod
 * Description: 映射器方法，是对 Mapper 接口方法的封装
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:33
 * Version: 1.0
 */
public class MapperMethod {

    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.command = new SqlCommand(configuration, mapperInterface, method);
    }

    /**
     * MapperProxy 调用该方法，执行具体的 SQL 语句
     */
    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (command.getType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                // todo:这里简单起见，直接调用selectOne方法，待完善。
                System.out.println("调用 SQL 语句的 SqlCommand 名称:" + command.getName());
                result = sqlSession.selectOne(command.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }

    /**
     * SQL 指令
     */
    public static class SqlCommand {
        /**
         * sql语句的唯一标识，格式:namespace.id，与 Mapper 接口中的方法一一对应
         */
        private final String name;

        /**
         * sql语句的类型，insert、delete、update、select
         */
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementId = mapperInterface.getName() + "." + method.getName();
            MappedStatement ms = configuration.getMappedStatement(statementId);
            name = ms.getId();
            type = ms.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }
}
