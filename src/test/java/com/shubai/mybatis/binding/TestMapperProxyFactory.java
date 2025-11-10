package com.shubai.mybatis.binding;

import com.shubai.mybatis.mapper.UserMapper;
import com.shubai.mybatis.session.SqlSession;
import com.shubai.mybatis.session.SqlSessionFactory;
import com.shubai.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;

import java.util.HashMap;

/**
 * ClassName: TestMapperProxyFactory
 * Description: 测试 MapperProxyFactory
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 14:34
 * Version: 1.0
 */
public class TestMapperProxyFactory {

    @Test
    public void test(){
        // 1. 使用 MapperRegistry 注册 Mapper
        MapperRegistry registry = new MapperRegistry();
        registry.addMappers("com.shubai.mybatis.mapper");

        // 2. 从 SqlSessionFactory 获取 SqlSession
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 3. 获取 Mapper 对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 4. 测试验证
        String result = userMapper.selectUserNameById("1");
        System.out.println(result);
    }
}
