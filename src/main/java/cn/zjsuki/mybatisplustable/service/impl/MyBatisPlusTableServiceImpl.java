package cn.zjsuki.mybatisplustable.service.impl;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlEntityCore;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlIndexCore;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlTableCore;
import cn.zjsuki.mybatisplustable.enums.DatabaseType;
import cn.zjsuki.mybatisplustable.service.MyBatisPlusTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @program: mybatis-plus-table
 * @description: 实现层
 * @author: LiYu
 * @create: 2023-06-13 11:39
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MyBatisPlusTableServiceImpl implements MyBatisPlusTableService {
    private final MysqlEntityCore mysqlEntityCore;
    private final MysqlIndexCore mysqlIndexCore;
    private final MysqlTableCore mysqlTableCore;
    private final MyBatisPlusTableConfig myBatisPlusTableConfig;

    @Override
    public Boolean createTable(Class<?> clazz) {
        if (Objects.requireNonNull(myBatisPlusTableConfig.getDatabaseType()) == DatabaseType.MYSQL) {
            return mysqlTableCore.createTable(null,clazz, null);
        }
        return false;
    }

    @Override
    public Boolean createTable(Class<?> clazz, String tableName) {
        if (Objects.requireNonNull(myBatisPlusTableConfig.getDatabaseType()) == DatabaseType.MYSQL) {
            return mysqlTableCore.createTable(null,clazz, tableName);
        }
        return false;
    }

    @Override
    public Boolean createTable(Class<?> clazz, Object tenantId) {
        String tableName = mysqlEntityCore.getEntityName(clazz, tenantId.toString());
        if (Objects.requireNonNull(myBatisPlusTableConfig.getDatabaseType()) == DatabaseType.MYSQL) {
            return mysqlTableCore.createTable(tenantId.toString(),clazz, tableName);
        }
        return false;
    }

    @Override
    public Boolean updateTable(Class<?> clazz) {
        return null;
    }

    @Override
    public Boolean createIndex(Class<?> clazz) {
        if (Objects.requireNonNull(myBatisPlusTableConfig.getDatabaseType()) == DatabaseType.MYSQL) {
            return mysqlIndexCore.createIndex(null,clazz, null);
        }
        return false;
    }

    @Override
    public Boolean createIndex(String tableName, String indexName, String columnName, String indexType) {
        if (Objects.requireNonNull(myBatisPlusTableConfig.getDatabaseType()) == DatabaseType.MYSQL) {
            return mysqlIndexCore.createIndex(null,tableName, indexName, columnName, indexType);
        }
        return false;
    }

    @Override
    public Boolean createIndex(Class<?> clazz, Long tenantId) {
        return null;
    }
}
