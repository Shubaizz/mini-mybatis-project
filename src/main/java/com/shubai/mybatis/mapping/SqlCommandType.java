package com.shubai.mybatis.mapping;

/**
 * ClassName: SqlCommandType
 * Description: SQL 语句的类型枚举
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:25
 * Version: 1.0
 */
public enum SqlCommandType {
    /**
     * 未知
     */
    UNKNOWN,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查找
     */
    SELECT
}
