package com.shubai.mybatis.session;

/**
 * ClassName: SqlSessionFactory
 * Description: 从连接或数据源创建 {@link SqlSession}
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 15:41
 * Version: 1.0
 */
public interface SqlSessionFactory {

    /**
     * 打开一个新的 {@link SqlSession} 连接
     */
    SqlSession openSession();
}
