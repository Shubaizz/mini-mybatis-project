package com.shubai.mybatis.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ClassName: TypeAliasRegistry
 * Description: 类型别名注册中心
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:03
 * Version: 1.0
 */
public class TypeAliasRegistry {

    /**
     * key: 别名（小写）
     * value: 对应的 Class 对象
     */
    private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    /**
     * 构造函数，注册系统内置的类型别名
     */
    public TypeAliasRegistry() {
        registerAlias("string", String.class);
        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);
    }

    /**
     * 注册类型别名
     *
     * @param alias 别名
     * @param value 对应的 Class 对象
     */
    public void registerAlias(String alias, Class<?> value) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        TYPE_ALIASES.put(key, value);
    }

    /**
     * 解析类型别名
     *
     * @param string 别名
     * @param <T>    Class 泛型
     * @return 对应的 Class 对象
     */
    public <T> Class<T> resolveAlias(String string) {
        String key = string.toLowerCase(Locale.ENGLISH);
        return (Class<T>) TYPE_ALIASES.get(key);
    }
}
