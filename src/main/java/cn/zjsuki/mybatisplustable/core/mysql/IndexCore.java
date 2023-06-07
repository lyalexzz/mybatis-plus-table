package cn.zjsuki.mybatisplustable.core.mysql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class IndexCore {
    private final JdbcTemplate jdbcTemplate;

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
    public void createIndex(String tableName, String indexName, String columnName, String indexType) {
        String sql = "ALTER TABLE " + tableName + " ADD INDEX " + indexName + " (" + columnName + ") USING " + indexType + ";";
        jdbcTemplate.execute(sql);
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
