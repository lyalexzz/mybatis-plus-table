package cn.zjsuki.mybatisplustable.config;

import cn.zjsuki.mybatisplustable.enums.DatabaseType;
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
    public Boolean enable;
    /**
     * 实体类包名
     */
    public String entityScan;
    /**
     * 数据库类型
     */
    public DatabaseType databaseType;
    /**
     * 是否开启驼峰标识
     */
    public Boolean hump;
    /**
     * 请求头中租户字段
     */
    public String tenantId;
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
}
