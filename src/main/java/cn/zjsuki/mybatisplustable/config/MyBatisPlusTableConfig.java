package cn.zjsuki.mybatisplustable.config;

import cn.zjsuki.mybatisplustable.enums.DatabaseType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: mybatis-plus-table
 * @description: 配置文件
 * @author: LiYu
 * @create: 2023-06-01 11:34
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "mybatis-plus-table-config")
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
}
