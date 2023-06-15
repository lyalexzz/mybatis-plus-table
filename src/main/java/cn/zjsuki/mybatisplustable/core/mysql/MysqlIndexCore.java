package cn.zjsuki.mybatisplustable.core.mysql;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
import cn.zjsuki.mybatisplustable.core.DatabaseCore;
import cn.zjsuki.mybatisplustable.core.EntityCore;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @program: mybatis-plus-table
 * @description: 索引
 * @author: LiYu
 * @create: 2023-06-07 17:41
 **/
@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan
public class MysqlIndexCore {
    private final JdbcTemplate jdbcTemplate;
    private final EntityCore entityCore;
    private final DatabaseCore databaseCore;

    /**
     * 创建索引
     *
     * @param tenantId   租户ID
     * @param tableName  表名
     * @param indexName  索引名
     * @param columnName 列名
     * @paran indexType 索引类型
     */
    public Boolean createIndex(String tenantId, String tableName, String indexName, String columnName, String indexType) {
        String sql = "ALTER TABLE " + tableName + " ADD INDEX " + indexName + " (" + columnName + ") USING " + indexType + ";";
        return databaseCore.execute(sql, tenantId);
    }

    /**
     * 创建索引
     *
     * @param tenantId  租户ID
     * @param clazz     类
     * @param tableName 表名
     * @return 结果
     */
    public Boolean createIndex(String tenantId, Class<?> clazz, String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            tableName = entityCore.getEntityName(clazz, null);
        }
        if (clazz.getAnnotation(IndexAop.class) != null) {
            String[] indexs = clazz.getAnnotation(IndexAop.class).value();
            for (String index : indexs) {
                String[] indexField = index.split(",");
                String indexName = indexField[0];
                String indexFieldStr = indexField[1];
                String indexType = indexField[2];
                createIndex(tenantId,tableName, indexName, indexFieldStr, indexType);
            }
        }
        return true;
    }

    /**
     * 获取表所有索引
     *
     * @param tableName 表名
     */
    public List<Map<String, Object>> getIndex(String tableName) {
        String sql = "SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema = (SELECT DATABASE()) AND table_name = ?";
        return jdbcTemplate.queryForList(sql, tableName);
    }
}
