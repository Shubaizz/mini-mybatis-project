package com.shubai.mybatis.session;

import com.shubai.mybatis.builder.xml.XMLConfigBuilder;
import com.shubai.mybatis.entity.User;
import com.shubai.mybatis.io.Resources;
import com.shubai.mybatis.session.defaults.DefaultSqlSession;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * ClassName: TestSqlSession
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 17:07
 * Version: 1.0
 */
public class TestSqlSession {

    @Test
    public void test() throws IOException {
        // 解析 XML 获取 Configuration
        Reader reader = Resources.getResourceAsReader("mini-mybatis-config.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 根据 Configuration 创建 DefaultSqlSession
        SqlSession sqlSession = new DefaultSqlSession(configuration);

        // 执行查询：默认是一个集合参数
        Object[] parameter = {1L};
        User user = sqlSession.selectOne("com.shubai.mybatis.mapper.UserMapper.selectById", parameter);
        System.out.println(user);
    }
}
