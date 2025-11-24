package com.shubai.mybatis.mapping;

import com.shubai.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * ClassName: Environment
 * Description: 解析 <environment> 标签后，封装成 Environment 对象，存储在 Configuration 中，主要存储数据源和事务工厂相关信息
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:38
 * Version: 1.0
 */
public class Environment {

    /**
     * 环境id
     */
    private final String id;

    /**
     * 事务工厂
     */
    private final TransactionFactory transactionFactory;

    /**
     * 数据源
     */
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {

        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource);
        }

    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
