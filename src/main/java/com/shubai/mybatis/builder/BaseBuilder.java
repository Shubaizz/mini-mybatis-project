package com.shubai.mybatis.builder;

import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.type.TypeAliasRegistry;

/**
 * ClassName: BaseBuilder
 * Description: 构建器基类
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:40
 * Version: 1.0
 */
public class BaseBuilder {

    /**
     * 待构建的配置对象
     */
    protected final Configuration configuration;

    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
