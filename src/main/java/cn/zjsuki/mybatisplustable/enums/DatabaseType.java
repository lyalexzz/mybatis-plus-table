package cn.zjsuki.mybatisplustable.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 数据库类型
 * @author: LiYu
 * @create: 2023-06-01 11:42
 **/
@Getter
@AllArgsConstructor
public enum DatabaseType {
    /**
     * mysql数据库
     */
    MYSQL(1, "MySQl"),

    /**
     * oracle数据库
     */
    ORACLE(2, "Oracle"),

    /**
     * sqlserver数据库
     */
    SQLSERVER(3, "SQLServer"),

    /**
     * postgresql数据库
     */
    POSTGRESQL(4, "PostgreSQL"),

    /**
     * db2数据库
     */
    DB2(5, "DB2"),

    /**
     * mariadb数据库
     */
    MARIADB(6, "MariaDB"),

    /**
     * sqlite数据库
     */
    SQLITE(7, "SQLite"),

    /**
     * h2数据库
     */
    H2(8, "H2"),

    /**
     * hsql数据库
     */
    HSQL(9, "HSQL");

    private final Integer value;

    private final String desc;
}
