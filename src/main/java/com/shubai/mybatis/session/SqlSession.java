package com.shubai.mybatis.session;

import java.util.List;

/**
 * ClassName: SqlSession
 * Description: 框架使用者操作的主要 Java 接口。通过此接口，可以执行SQL语句。
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/10 15:39
 * Version: 1.0
 */
public interface SqlSession {

    /**
     * 从一个“语句ID”中，获取一条记录（比如，查询一个用户的信息）。
     *
     * @param <T>       返回的数据类型（比如，如果你查的是用户，T就是User类）
     * @param statement 用来找到要执行的SQL语句的唯一标识符（可以理解为SQL语句的名字）
     * @return 映射好的对象（比如，一个User对象）
     */
    <T> T selectOne(String statement);

    /**
     * 从一个“语句ID”和“参数”中，获取一条记录。
     * 比如，查询ID为1的用户，这里的1就是参数。
     *
     * @param <T>       返回的数据类型
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @param parameter 传递给SQL语句的参数对象（比如，用户的ID）
     * @return 映射好的对象
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * 从一个“语句ID”中，获取多条记录（比如，查询所有用户的信息）。
     *
     * @param <E>       返回的列表里，每个元素的数据类型（比如，如果你查的是用户列表，E就是User类）
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @return 映射好的对象列表（比如，一个List<User>）
     */
    <E> List<E> selectList(String statement);

    /**
     * 从一个“语句ID”和“参数”中，获取多条记录。
     * 比如，查询所有年龄大于18的用户。
     *
     * @param <E>       返回的列表里，每个元素的数据类型
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @param parameter 传递给SQL语句的参数对象
     * @return 映射好的对象列表
     */
    <E> List<E> selectList(String statement, Object parameter);

    /**
     * 执行一个插入（insert）语句。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @return 整数，表示这次插入操作影响了多少行数据（比如，成功插入了一条数据，就返回1）
     */
    int insert(String statement);

    /**
     * 执行一个带有参数的插入（insert）语句。
     * 如果数据库有自动增长的ID（比如自增主键），或者你设置了特殊的“selectKey”来获取插入后的值，
     * 那么这些值会更新到你传入的参数对象中。
     * 这个方法只会返回受影响的行数。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @param parameter 传递给SQL语句的参数对象（比如，要插入的用户信息）
     * @return 整数，表示这次插入操作影响了多少行数据
     */
    int insert(String statement, Object parameter);

    /**
     * 执行一个更新（update）语句。会返回受影响的行数。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @return 整数，表示这次更新操作影响了多少行数据
     */
    int update(String statement);

    /**
     * 执行一个带有参数的更新（update）语句。会返回受影响的行数。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @param parameter 传递给SQL语句的参数对象
     * @return 整数，表示这次更新操作影响了多少行数据
     */
    int update(String statement, Object parameter);

    /**
     * 执行一个删除（delete）语句。会返回受影响的行数。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @return 整数，表示这次删除操作影响了多少行数据
     */
    int delete(String statement);

    /**
     * 执行一个带有参数的删除（delete）语句。会返回受影响的行数。
     *
     * @param statement 用来找到要执行的SQL语句的唯一标识符
     * @param parameter 传递给SQL语句的参数对象
     * @return 整数，表示这次删除操作影响了多少行数据
     */
    int delete(String statement, Object parameter);

    /**
     * 获取一个“映射器”（Mapper）。
     * 映射器是一个接口，里面定义了你所有的数据库操作方法，通过它你可以更方便地执行SQL。
     *
     * @param <T>  映射器接口的类型
     * @param type 映射器接口的Class对象
     * @return 一个绑定到当前SqlSession的映射器实例
     */
    <T> T getMapper(Class<T> type);
}
