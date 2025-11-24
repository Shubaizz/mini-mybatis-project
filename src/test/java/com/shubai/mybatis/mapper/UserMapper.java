package com.shubai.mybatis.mapper;

import com.shubai.mybatis.entity.User;

/**
 * ClassName: UserMapper
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 14:31
 * Version: 1.0
 */
public interface UserMapper {

    User selectById(String id);
}
