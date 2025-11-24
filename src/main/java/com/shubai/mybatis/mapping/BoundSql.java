package com.shubai.mybatis.mapping;

import java.util.Map;

/**
 * ClassName: BoundSql
 * Description: SQL 语句及其相关信息的封装类，表示已经处理好的 SQL 语句，包括占位符替换等信息。
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:41
 * Version: 1.0
 */
public class BoundSql {

    /**
     * 处理后的 SQL 语句，可能包含 "?" 占位符
     */
    private String sql;

    /**
     * 参数映射，key 是参数位置（从1开始），value 是参数名称
     */
    private Map<Integer, String> parameterMappings;

    /**
     * 参数类型
     */
    private String parameterType;

    /**
     * 结果类型
     */
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }
}
