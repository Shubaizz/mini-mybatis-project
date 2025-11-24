package com.shubai.mybatis.session;

import com.shubai.mybatis.entity.User;
import com.shubai.mybatis.io.Resources;
import com.shubai.mybatis.mapper.UserMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * ClassName: TestSqlSessionFactoryBuilder
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:50
 * Version: 1.0
 */
public class TestSqlSessionFactoryBuilder {

    @Test
    public void test() throws IOException {
        // 1. 从SqlSessionFactory 中获取 SqlSession
        Reader reader = Resources.getResourceAsReader("mini-mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 3. 测试验证
        User user = userMapper.selectById("1");
        System.out.println(user);
    }

}
