package cn.zjsuki.mybatisplustable.utils;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.core.DatabaseCore;
import cn.zjsuki.mybatisplustable.core.EntityCore;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlTableCore;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mybatis-plus-table
 * @description: Mysql
 * @author: LiYu
 * @create: 2023-06-15 15:19
 **/
@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan
public class MysqlStart {
    private final EntityCore entityCore;
    private final MyBatisPlusTableConfig myBatisPlusTableConfig;
    private final MysqlTableCore mysqlTableCore;
    private final DatabaseCore databaseCore;

    /**
     * 执行
     */
    public void start() {
        databaseCore.check();
        List<String> tenantIdList = myBatisPlusTableConfig.getTenantIdList();
        if (tenantIdList.size() == 0) {
            tenantIdList.add("");
        }
        //开始扫描表
        List<Class<?>> entityList = EntityCore.scanPackageForEntities(myBatisPlusTableConfig.getEntityScan());
        if (entityList.size() == 0) {
            log.error("没有扫描到实体类");
            return;
        }
        tenantIdList.forEach(tenantId -> {
            for (Class<?> clazz : entityList) {
                //判断表是否存在
                String name = entityCore.getEntityName(clazz, tenantId);
                if (mysqlTableCore.isTableExist(tenantId,name)) {
                    //获取实体类的所有字段
                    List<Field> fieldList = EntityCore.getAllFields(clazz);
                    List<Field> fieldName = new ArrayList<>();
                    fieldList.forEach(val -> {
                        if ("serialVersionUID".equals(val.getName())) {
                            return;
                        }
                        TableField tableField = val.getAnnotation(TableField.class);
                        if (tableField != null && tableField.exist()) {
                            fieldName.add(val);
                        }
                        if (tableField == null) {
                            fieldName.add(val);
                        }
                    });
                    //判断实体类里面的字段是否都存在
                    List<Field> getNotExitColumn;
                    try {
                        getNotExitColumn = mysqlTableCore.getNotExitColumn(tenantId, name, fieldName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (getNotExitColumn.size() > 0) {
                        //开始创建字段
                        mysqlTableCore.createColumn(tenantId, name, getNotExitColumn);
                    }
                    List<String> getNotExitTable = null;
                    try {
                        getNotExitTable = mysqlTableCore.getNotExitField(tenantId, name, fieldName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (getNotExitTable.size() > 0) {
                        //开始删除字段
                        mysqlTableCore.deleteColumn(tenantId, name, getNotExitTable);
                    }
                    //创建索引
                    mysqlTableCore.createIndex(tenantId,clazz);
                } else {
                    log.info("表{}不存在，开始创建表", name);
                    mysqlTableCore.createTable(tenantId, clazz, name);
                }
            }
        });
    }


}
