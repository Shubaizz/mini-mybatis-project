package com.shubai.mybatis.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: JdbcType
 * Description: JDBC 类型枚举
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 16:01
 * Version: 1.0
 */
public enum JdbcType {

    /**
     * JDBC 类型枚举，对应 java.sql.Types 中的常量
     */
    INTEGER(Types.INTEGER),
    FLOAT(Types.FLOAT),
    DOUBLE(Types.DOUBLE),
    DECIMAL(Types.DECIMAL),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP);

    /**
     * 枚举类的成员变量，存储对应的 JDBC 类型代码
     */
    public final int TYPE_CODE;

    JdbcType(int code) {
        this.TYPE_CODE = code;
    }

    /**
     * 静态 HashMap，用于根据 JDBC 类型代码查找对应的枚举实例
     */
    private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

    /**
     * 静态代码块，在类加载时执行，初始化 codeLookup 映射
     */
    static {
        for (JdbcType type : JdbcType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    public static JdbcType forCode(int code) {
        return codeLookup.get(code);
    }
}
