package cn.zjsuki.mybatisplustable.config;

import cn.zjsuki.mybatisplustable.enums.DatabaseType;
import cn.zjsuki.mybatisplustable.enums.OperationModeType;
import cn.zjsuki.mybatisplustable.enums.TenantFollowType;
import cn.zjsuki.mybatisplustable.enums.TenantType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: mybatis-plus-table
 * @description: 配置文件
 * @author: LiYu
 * @create: 2023-06-01 11:34
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "mybatis-plus-table-config")
@ComponentScan
public class MyBatisPlusTableConfig {
    /**
     * 是否启用
     */
    public Boolean enable = false;
    /**
     * 实体类包名
     */
    public String entityScan;
    /**
     * 数据库类型
     */
    public DatabaseType databaseType = DatabaseType.MYSQL;
    /**
     * 是否开启驼峰标识
     */
    public Boolean hump = true;
    /**
     * 租户列表
     */
    public List<String> tenantIdList = new ArrayList<>();
    /**
     * 租户类型
     */
    public TenantType tenantType = TenantType.NONE;
    /**
     * 租户数据源列表
     */
    public Map<String, Map<String, String>> tenantDatabaseList;
    /**
     * 操作方式
     */
    public OperationModeType operationMode = OperationModeType.AT_STARTUP;
    /**
     * 租户跟随方式
     */
    public TenantFollowType tenantFollowType = TenantFollowType.SUFFIX;
}
