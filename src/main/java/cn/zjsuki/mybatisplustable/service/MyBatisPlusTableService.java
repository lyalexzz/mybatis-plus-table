package cn.zjsuki.mybatisplustable.service;

/**
 * @program: mybatis-plus-table
 * @description: 接口类
 * @author: LiYu
 * @create: 2023-06-13 11:30
 **/
public interface MyBatisPlusTableService {
    /**
     * 创建表
     *
     * @param clazz 类
     * @return 是否创建成功
     */
    Boolean createTable(Class<?> clazz);

    /**
     * 创建表（自定义表名）
     *
     * @param clazz     类
     * @param tableName 表名
     * @return 是否创建成功
     */
    Boolean createTable(Class<?> clazz, String tableName);

    /**
     * 创建表（根据租户ID）
     *
     * @param clazz    类
     * @param tenantId 租户ID
     * @return 是否创建成功
     */
    Boolean createTable(Class<?> clazz, Object tenantId);

    /**
     * 更新表
     *
     * @param clazz 类
     * @return 是否更新成功
     */
    Boolean updateTable(Class<?> clazz);

    /**
     * 创建索引
     *
     * @param clazz 类
     * @return 是否创建成功
     */
    Boolean createIndex(Class<?> clazz);

    /**
     * 创建索引（自定义索引名）
     *
     * @param tableName  表名
     * @param indexName  索引名
     * @param columnName 列名
     * @param indexType  索引类型
     * @return 是否创建成功
     */
    Boolean createIndex(String tableName, String indexName, String columnName, String indexType);

    /**
     * 创建索引（根据租户ID）
     *
     * @param clazz    类
     * @param tenantId 租户ID
     * @return 是否创建成功
     */
    Boolean createIndex(Class<?> clazz, Long tenantId);
}
