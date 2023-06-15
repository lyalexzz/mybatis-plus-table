package cn.zjsuki.mybatisplustable.service.impl;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.core.mysql.EntityCore;
import cn.zjsuki.mybatisplustable.core.mysql.IndexCore;
import cn.zjsuki.mybatisplustable.core.mysql.TableCore;
import cn.zjsuki.mybatisplustable.service.MyBatisPlusTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final EntityCore entityCore;
    private final IndexCore indexCore;
    private final TableCore tableCore;
    private final MyBatisPlusTableConfig myBatisPlusTableConfig;

    @Override
    public Boolean createTable(Class<?> clazz) {
        return tableCore.createTable(clazz, null);
    }

    @Override
    public Boolean createTable(Class<?> clazz, String tableName) {
        return tableCore.createTable(clazz, tableName);
    }

    @Override
    public Boolean createTable(Class<?> clazz, Object tenantId) {
        String tableName = entityCore.getEntityName(clazz, tenantId.toString());
        return tableCore.createTable(clazz, tableName);
    }

    @Override
    public Boolean updateTable(Class<?> clazz) {
        return null;
    }

    @Override
    public Boolean createIndex(Class<?> clazz) {
        return indexCore.createIndex(clazz, null);
    }

    @Override
    public Boolean createIndex(String tableName, String indexName, String columnName, String indexType) {
        return indexCore.createIndex(tableName, indexName, columnName, indexType);
    }

    @Override
    public Boolean createIndex(Class<?> clazz, Long tenantId) {
        return null;
    }
}
