package com.shubai.mybatis.session.defaults;

import com.shubai.mybatis.binding.MapperRegistry;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.session.SqlSession;

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
        return Collections.emptyList();
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
