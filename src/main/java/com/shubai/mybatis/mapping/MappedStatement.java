package com.shubai.mybatis.mapping;

import com.shubai.mybatis.session.Configuration;

import java.util.Map;

/**
 * ClassName: MappedStatement
 * Description: SQL 映射语句的封装类，包含了 SQL 语句及其相关的配置信息
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:25
 * Version: 1.0
 */
public class MappedStatement {
    /**
     * 全局配置对象
     */
    private Configuration configuration;

    /**
     * MappedStatement 的唯一标识符
     */
    private String id;

    /**
     * SQL 语句的类型（如 SELECT、INSERT、UPDATE、DELETE）
     */
    private SqlCommandType sqlCommandType;

    /**
     * 对应标签中 SQL 语句解析后的封装类对象
     */
    private BoundSql boundSql;

    MappedStatement() {
        // constructor disabled
    }

    /**
     * 建造者
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, BoundSql boundSql) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.boundSql = boundSql;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }
}
