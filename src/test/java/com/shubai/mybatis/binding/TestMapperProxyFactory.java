package com.shubai.mybatis.binding;

import com.shubai.mybatis.mapper.UserMapper;
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
        // 创建 MapperProxyFactory 对象
        MapperProxyFactory<UserMapper> mapperProxyFactory = new MapperProxyFactory<>(UserMapper.class);
        // 创建 sqlSession 对象
        HashMap<String , String> sqlSession = new HashMap<>();
        // 填充 sqlSession 数据
        sqlSession.put("com.shubai.mybatis.mapper.UserMapper.selectUserNameById","SELECT username FROM users WHERE id = ?");
        // 获取 MapperProxy 对象
        UserMapper userMapper = mapperProxyFactory.newInstance(sqlSession);
        // 调用方法，触发代理逻辑
        String result = userMapper.selectUserNameById("1");
        System.out.println(result);
    }
}
