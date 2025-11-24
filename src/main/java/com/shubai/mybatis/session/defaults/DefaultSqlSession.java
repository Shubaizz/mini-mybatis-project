package com.shubai.mybatis.session.defaults;

import com.shubai.mybatis.binding.MapperRegistry;
import com.shubai.mybatis.mapping.BoundSql;
import com.shubai.mybatis.mapping.Environment;
import com.shubai.mybatis.mapping.MappedStatement;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName: DefaultSqlSession
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 15:42
 * Version: 1.0
 */
public class DefaultSqlSession implements SqlSession {

    // 全局配置对象
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = selectList(statement, parameter);
        if (list.size() == 1){
            return list.get(0);
        } else if (list.size() > 1){
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        try {
            // 根据 statement 从配置中获取对应的 MappedStatement 对象
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            // 获取环境配置对象
            Environment environment = configuration.getEnvironment();
            // 从环境配置中获取数据库连接
            Connection connection = environment.getDataSource().getConnection();
            // 获取 BoundSql 对象，包含了 SQL 语句和相关信息
            BoundSql boundSql = mappedStatement.getBoundSql();
            // 创建 PreparedStatement 对象，准备执行 SQL 语句
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            // todo:设置参数部分这里存在一些问题，这里先简单处理，只设置一个类型为 Long 的参数，如果测试传入无法转换为 Long 类型的参数会报错，待完善。
            preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
            // 调用 JDBC API 执行查询
            ResultSet resultSet = preparedStatement.executeQuery();
            // 将结果集转换为对象列表
            List<E> objList = resultSet2ObjectList(resultSet, Class.forName(boundSql.getResultType()));
            return objList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将结果集转换为对象列表
     *
     * @param resultSet 结果集
     * @param clazz     目标对象的类
     * @param <E>       对象类型
     * @return 对象列表
     */
    private <E> List<E> resultSet2ObjectList(ResultSet resultSet, Class<?> clazz) {
        // 用于存储结果对象的列表
        List<E> list = new ArrayList<>();
        try {
            // 获取结果集元数据，其中包含字段名，字段值等信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 获取结果集中的列数
            int columnCount = metaData.getColumnCount();
            // 遍历结果集的每一行，TODO:ResultSet 是游标类型，需要不断调用 next() 方法将游标向下移动，直到没有更多行。
            while (resultSet.next()) {
                // 使用反射创建目标对象的实例
                E obj = (E) clazz.newInstance();
                // 遍历每一列，将列值设置到目标对象的对应属性中
                for (int i = 1; i <= columnCount; i++) {
                    // todo:这里直接使用 getObject 方法获取列值，之后可以根据具体的类型获取对应的值，待完善。
                    Object value = resultSet.getObject(i);
                    // 获取列名，并根据列名构建对应的 setter 方法名
                    String columnName = metaData.getColumnName(i);
                    // 构建 setter 方法名，例如列名为 "name"，则方法名为 "setName"，TODO:如果返回值类型中没有对应的 setter 方法会报错，且当前 mapper.xml 中 resultType 只能是实体类，不能是基本类型，待完善。
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;
                    // TODO: 对不同类型进行处理，这里只简单处理了 Date 类型，其他类型可以根据需要添加对应的处理逻辑。
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, Date.class);
                    } else {
                        method = clazz.getMethod(setMethod, value.getClass());
                    }
                    method.invoke(obj, value);
                }
                // 将填充好的对象添加到结果列表中
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int insert(String statement) {
        return insert(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    @Override
    public int update(String statement, Object parameter) {
        return 0;
    }

    @Override
    public int delete(String statement) {
        return update(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> mapperClass) {
        return configuration.getMapper(mapperClass, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
