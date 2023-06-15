package cn.zjsuki.mybatisplustable.core.oracle;

import cn.zjsuki.mybatisplustable.core.DatabaseCore;
import cn.zjsuki.mybatisplustable.core.EntityCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: mybatis-plus-table
 * @description: oracle表
 * @author: LiYu
 * @create: 2023-06-15 17:44
 **/
@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan
public class OracleTableCore {
    private final EntityCore entityCore;
    private final DatabaseCore databaseCore;

    /**
     * 根据columnNames获取Oracle表中不存在的列
     * @param tenantId 租户ID
     * @param tableName 表名
     * @param columnNames 列名
     * @return 结果
     */
    public List<Field> getNotExitColumn(String tenantId, String tableName, List<Field> columnNames) {
        String sql = "select column_name from user_tab_columns where table_name = '" + tableName + "'";
        List<String> columnList = (List<String>) databaseCore.executeList(sql, tenantId, String.class);
        columnNames.removeIf(field -> columnList.contains(field.getName()));
        return columnNames;
    }
}
