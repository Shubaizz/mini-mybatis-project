package com.shubai.mybatis.builder.xml;

import com.shubai.mybatis.builder.BaseBuilder;
import com.shubai.mybatis.datasource.DataSourceFactory;
import com.shubai.mybatis.io.Resources;
import com.shubai.mybatis.mapping.BoundSql;
import com.shubai.mybatis.mapping.Environment;
import com.shubai.mybatis.mapping.MappedStatement;
import com.shubai.mybatis.mapping.SqlCommandType;
import com.shubai.mybatis.session.Configuration;
import com.shubai.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: XMLConfigBuilder
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/11/24 14:41
 * Version: 1.0
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {
        // 调用父类构造方法初始化Configuration
        super(new Configuration());
        // 使用 dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析配置：类型别名、插件、对象工厂、对象包装工厂、设置、环境、类型转换、映射器
     *
     * @return Configuration
     */
    public Configuration parse() {
        try {
            // 解析 <environments> 节点，配置数据源和事务管理器
            environmentsElement(root.element("environments"));
            // 解析 <mappers> 节点，注册 Mapper 映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    /**
     * 解析 <environments> 节点，配置数据源和事务管理器
     */
    private void environmentsElement(Element environments) throws Exception {
        // 获取 default 属性值，表示默认使用哪个环境
        String environment = environments.attributeValue("default");
        // 获取所有的 <environment> 子节点
        List<Element> environmentElementList = environments.elements("environment");
        // 遍历 <environments> 节点下的所有子节点
        for (Element environmentElement : environmentElementList) {
            // 获取 id 属性值，和 default 属性值进行比较，找到需要使用的环境
            String id = environmentElement.attributeValue("id");
            if (environment.equals(id)) {
                // 根据 <environment> 标签的 <transactionManager> 子标签中的 type 属性值，从 typeAliasRegistry 中获取 TransactionFactory 对应的 Class 对象，然后通过 newInstance 创建对象
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(environmentElement.element("transactionManager").attributeValue("type")).newInstance();
                // 获取 <environment> 标签中的 <dataSource> 子标签
                Element dataSourceElement = environmentElement.element("dataSource");
                // 根据 <dataSource> 标签中的 type 属性值，从 typeAliasRegistry 中获取 DataSourceFactory 对应的 Class 对象，然后通过 newInstance 创建对象
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
                // 解析 <dataSource> 标签中的所有 <property> 子标签，封装成 Properties 对象
                List<Element> propertyElementList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element propertyElement : propertyElementList) {
                    props.setProperty(propertyElement.attributeValue("name"), propertyElement.attributeValue("value"));
                }
                // 使用配置的属性值，创建数据源
                dataSourceFactory.setProperties(props);
                DataSource dataSource = dataSourceFactory.getDataSource();
                // 创建 Environment.Builder，并设置 id、事务管理器和数据源
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);
                // 构建 Environment 对象，并设置到 configuration 中
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }


    /**
     * 解析 <mappers> 节点，注册 Mapper 映射器
     */
    private void mapperElement(Element mappers) throws Exception {
        // 获取所有的 <mapper> 子节点
        List<Element> mapperElementList = mappers.elements("mapper");
        // 遍历 <mappers> 节点下的所有子节点
        for (Element mapperElement : mapperElementList) {
            // 获取 resource 属性值，找到对应的 Mapper 映射文件，进行解析
            String resource = mapperElement.attributeValue("resource");
            // 获取 mapper.xml 文件的 Reader 对象
            Reader reader = Resources.getResourceAsReader(resource);
            // 使用 dom4j 解析 mapper.xml 文件
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(reader));

            // 获取 mapper.xml 文件的根节点 <mapper>
            Element root = document.getRootElement();

            // 获取命名空间
            String namespace = root.attributeValue("namespace");

            // 解析 <select> 节点，封装 MappedStatement 对象
            List<Element> selectElementList = root.elements("select");
            for (Element selectElement : selectElementList) {
                // 获取 id、parameterType、resultType、sql 语句
                String id = selectElement.attributeValue("id");
                String parameterType = selectElement.attributeValue("parameterType");
                String resultType = selectElement.attributeValue("resultType");
                String sql = selectElement.getText();

                // #{} 参数处理
                // 创建一个用于存储参数索引和参数名称的映射表
                Map<Integer, String> parameter = new HashMap<>();
                // 定义一个正则表达式，用于匹配 SQL 语句中的 #{参数名} 形式
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                // 用正则表达式去匹配 SQL 语句，得到一个匹配器对象
                Matcher matcher = pattern.matcher(sql);
                // 遍历所有匹配到的 #{参数名}，i 从 1 开始计数
                for (int i = 1; matcher.find(); i++) {
                    // 获取完整的匹配内容，比如 #{id}
                    String g1 = matcher.group(1);
                    // 获取参数名，比如 id
                    String g2 = matcher.group(2);
                    // 将参数索引和参数名存入映射表
                    parameter.put(i, g2);
                    // 将 SQL 语句中的 #{参数名} 替换为 ?，用于预编译 SQL
                    sql = sql.replace(g1, "?");
                }

                // 构建 MappedStatement 对象
                // 根据命名空间和 id 拼接成唯一的 statementId
                String statementId = namespace + "." + id;
                // 获取 SQL 语句的类型，比如 select、insert、update、delete
                String selectElementName = selectElement.getName();
                // 将 SQL 语句类型转换为枚举类型 SqlCommandType
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(selectElementName.toUpperCase(Locale.ENGLISH));
                // 创建 BoundSql 对象，封装处理后的 SQL 语句、参数映射表、参数类型和结果类型
                BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);
                // 使用 MappedStatement.Builder 构建 MappedStatement 对象
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, statementId, sqlCommandType, boundSql).build();
                // 将解析得到的 MappedStatement 对象存入 Configuration 中 mappedStatements 集合中
                configuration.addMappedStatement(mappedStatement);
            }

            // todo:这里可以继续解析 <insert>、<update>、<delete> 节点，封装 MappedStatement 对象，类似于上面的 <select> 节点解析，待完善。

            // 注册Mapper映射器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
