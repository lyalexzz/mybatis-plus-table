package cn.zjsuki.mybatisplustable.core.mysql;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
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
public class IndexCore {
    private final JdbcTemplate jdbcTemplate;
    private final EntityCore entityCore;

    /**
     * 表是否存在索引
     *
     * @param tableName 表名
     * @param indexName 索引名
     */
    public Boolean indexExit(String tableName, String indexName) {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema = (SELECT DATABASE()) AND table_name = ? AND index_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, indexName);
        return count != null && count > 0;
    }

    /**
     * 创建索引
     *
     * @param tableName  表名
     * @param indexName  索引名
     * @param columnName 列名
     * @paran indexType 索引类型
     */
    public Boolean createIndex(String tableName, String indexName, String columnName, String indexType) {
        String sql = "ALTER TABLE " + tableName + " ADD INDEX " + indexName + " (" + columnName + ") USING " + indexType + ";";
        jdbcTemplate.execute(sql);
        return true;
    }

    /**
     * 创建索引
     *
     * @param clazz     类
     * @param tableName 表名
     * @return 结果
     */
    public Boolean createIndex(Class<?> clazz, String tableName) {
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
                createIndex(tableName, indexName, indexFieldStr, indexType);
            }
        }
        return true;
    }

    /**
     * 删除索引
     *
     * @param tableName 表名
     * @param indexName 索引名
     */
    public void dropIndex(String tableName, String indexName) {
        String sql = "ALTER TABLE " + tableName + " DROP INDEX " + indexName;
        jdbcTemplate.execute(sql);
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
