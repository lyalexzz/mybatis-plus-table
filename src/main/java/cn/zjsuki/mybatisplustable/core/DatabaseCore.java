package cn.zjsuki.mybatisplustable.core;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.enums.TenantType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final JdbcTemplate jdbcTemplate;

    /**
     * 租户校验
     */
    public void check() {
        if (config.getTenantType().equals(TenantType.DATASOURCE)) {
            log.info("基于数据源的多租户模式");
            if (config.getTenantIdList().size() != config.getTenantDatabaseList().size()) {
                throw new RuntimeException("数据源数量与租户ID数量不一致");
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
     * 执行SQL
     *
     * @param sql      SQL语句
     * @param tenantId 租户ID
     * @return 是否执行成功
     */
    public Boolean execute(String sql, String tenantId) {
        if (Objects.requireNonNull(config.getTenantType()) == TenantType.DATASOURCE && StringUtils.isNotBlank(tenantId)) {
            JdbcTemplate myJdbcTemplate = new JdbcTemplate(dataSources.get(tenantId));
            myJdbcTemplate.execute(sql);
        } else {
            jdbcTemplate.execute(sql);
        }
        return true;
    }

    /**
     * 执行sql 并且获取指定类型的值
     *
     * @param sql      sql语句
     * @param tenantId 租户ID
     * @param clazz    指定类型
     * @param <T>      泛型
     */
    public <T> T execute(String sql, String tenantId, Class<T> clazz) {
        if (Objects.requireNonNull(config.getTenantType()) == TenantType.DATASOURCE && StringUtils.isNotBlank(tenantId)) {
            JdbcTemplate myJdbcTemplate = new JdbcTemplate(dataSources.get(tenantId));
            return myJdbcTemplate.queryForObject(sql, clazz);
        } else {
            return jdbcTemplate.queryForObject(sql, clazz);
        }
    }

    /**
     * 执行sql 并且获取指定类型的List值
     *
     * @param sql      sql语句
     * @param tenantId 租户ID
     * @param clazz    指定类型
     * @return List 结果
     */
    public List<?> executeList(String sql, String tenantId, Class<?> clazz) {
        if (Objects.requireNonNull(config.getTenantType()) == TenantType.DATASOURCE && StringUtils.isNotBlank(tenantId)) {
            JdbcTemplate myJdbcTemplate = new JdbcTemplate(dataSources.get(tenantId));
            return myJdbcTemplate.queryForList(sql, clazz);
        } else {
            return jdbcTemplate.queryForList(sql, clazz);
        }
    }

    /**
     * 根据租户id获取Connection
     *
     * @param tenantId 租户ID
     * @return Connection
     */
    public Connection getConnection(String tenantId) throws SQLException {
        if (Objects.requireNonNull(config.getTenantType()) == TenantType.DATASOURCE && StringUtils.isNotBlank(tenantId)) {
            return dataSources.get(tenantId).getConnection();
        } else {
            return Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        }
    }
}
