package cn.zjsuki.mybatisplustable.core.mysql;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.enums.TenantType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: mybatis-plus-table
 * @description: 数据源核心类
 * @author: LiYu
 * @create: 2023-06-08 18:20
 **/
@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan
public class DatabaseCore {
    private final MyBatisPlusTableConfig config;
    private final Map<String, DataSource> dataSources = new HashMap<>();

    /**
     * 租户校验
     */
    public void check() {
        if (config.getTenantType().equals(TenantType.DATASOURCE)) {
            log.info("基于数据源的多租户模式");
            if (config.getTenantIdList().size() != config.getTenantDatabaseList().size()) {
                throw new RuntimeException("数据源数量与租户ID数量不一致");
            }
        }
        //开始根据租户创建数据源
        for (int i = 0; i < config.getTenantIdList().size(); i++) {
            String tenantId = config.getTenantIdList().get(i);
            String url = config.getTenantDatabaseList().get(tenantId).get("url");
            String username = config.getTenantDatabaseList().get(tenantId).get("username");
            String password = config.getTenantDatabaseList().get(tenantId).get("password");
            if (config.getTenantType().equals(TenantType.DATASOURCE)) {
                createDataSource(tenantId, url, username, password);
            }
        }
    }

    /**
     * 创建数据源
     *
     * @param tenantId 租户ID
     * @param url      数据库连接
     * @param username 用户名
     * @param password 密码
     */
    public void createDataSource(String tenantId, String url, String username, String password) {
        //根据账号密码以及链接创建数据源
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 初始连接池大小
        dataSource.setInitialSize(10);
        // 最大连接数
        dataSource.setMaxTotal(100);
        // 最大空闲连接数
        dataSource.setMaxIdle(20);
        dataSources.put(tenantId, dataSource);
    }

    /**
     * 根据租户ID执行不同数据源SQL
     *
     * @param tenantId 租户ID
     * @param sql      SQL语句
     */
    public void executeSql(String tenantId, String sql) {
        DataSource dataSource = dataSources.get(tenantId);
        if (dataSource == null) {
            throw new RuntimeException("数据源不存在");
        }
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 获取数据库连接
            connection = dataSource.getConnection();
            // 创建Statement对象
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            // 处理查询结果
            while (resultSet.next()) {
                // 从resultSet中获取数据
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                // ...

                // 处理数据
                System.out.println("Name: " + name + ", Email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
