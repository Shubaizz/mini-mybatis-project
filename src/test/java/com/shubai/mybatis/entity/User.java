package com.shubai.mybatis.entity;

import lombok.Data;

/**
 * ClassName: User
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:54
 * Version: 1.0
 */
@Data
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名称
     */
    private String username;
}
