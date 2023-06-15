package cn.zjsuki.mybatisplustable.core.oracle;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
import cn.zjsuki.mybatisplustable.core.DatabaseCore;
import cn.zjsuki.mybatisplustable.core.EntityCore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
     *
     * @param tenantId    租户ID
     * @param tableName   表名
     * @param columnNames 列名
     * @return 结果
     */
    public List<Field> getNotExitColumn(String tenantId, String tableName, List<Field> columnNames) {
        String sql = "select column_name from user_tab_columns where table_name = '" + tableName + "'";
        List<String> columnList = (List<String>) databaseCore.executeList(sql, tenantId, String.class);
        columnNames.removeIf(field -> columnList.contains(field.getName()));
        return columnNames;
    }

    /**
     * 获取表结构存在的字段但是实体类中不存在的字段
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public List<String> getNotExitField(String tenantId, String tableName, List<String> fieldList) {
        String sql = "select column_name from user_tab_columns where table_name = '" + tableName + "'";
        List<String> columnList = (List<String>) databaseCore.executeList(sql, tenantId, String.class);
        fieldList.removeIf(columnList::contains);
        return fieldList;
    }

    /**
     * 通过Class以及他的mybatis-plus注解创建O表
     *
     * @param tenantId  租户id
     * @param clazz     类
     * @param tableName 表名
     * @return 是否创建成功
     */
    public Boolean createTable(String tenantId, Class<?> clazz, String tableName) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer stringBuffer = new StringBuffer();
        //开始组装oracle建表SQL 并且字段不同类型对应不同的SQL
        stringBuffer.append("create table ").append(tableName).append("(");
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            TableField annotation = field.getAnnotation(TableField.class);
            if (annotation != null && !annotation.exist()) {
                continue;
            }
            String type = field.getType().getSimpleName();
            String name = field.getName();
            switch (type) {
                case "String":
                    stringBuffer.append(name).append(" varchar2(255),");
                    break;
                case "Integer":
                    stringBuffer.append(name).append(" number(10),");
                    break;
                case "Long":
                    stringBuffer.append(name).append(" number(20),");
                    break;
                case "Date":
                    stringBuffer.append(name).append(" date,");
                    break;
                case "Double":
                case "BigDecimal":
                    stringBuffer.append(name).append(" number(20,2),");
                    break;
                case "Boolean":
                    stringBuffer.append(name).append(" number(1),");
                    break;
                default:
                    log.error("不支持的类型：" + type);
                    return false;
            }
        }
        stringBuffer.append(")");
        String autoIncrementSql = "";
        //获取携带TableId的字段，并且为他添加主键以及根据主键生成策略
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            TableField annotation = field.getAnnotation(TableField.class);
            if (annotation != null && !annotation.exist()) {
                continue;
            }
            if (field.getAnnotation(com.baomidou.mybatisplus.annotation.TableId.class) != null) {
                String type = field.getType().getSimpleName();
                String name = field.getName();
                switch (type) {
                    case "String":
                    case "Integer":
                    case "Long":
                        stringBuffer.append("alter table ").append(tableName).append(" add constraint ").append(tableName).append("_pk primary key (").append(name).append(")");
                        break;
                    default:
                        log.error("不支持的类型：" + type);
                        return false;
                }
                if (field.getAnnotation(com.baomidou.mybatisplus.annotation.TableId.class).type() == com.baomidou.mybatisplus.annotation.IdType.AUTO) {
                    autoIncrementSql = "create sequence " + tableName + "_seq increment by 1 start with 1 nomaxvalue";
                }
                log.info("TableMaintenanceService.createTable sql:{}", stringBuffer);
                //开始执行sql语句
                databaseCore.execute(stringBuffer.toString(), tenantId);
                if (StringUtils.isNotEmpty(autoIncrementSql)) {
                    databaseCore.execute(autoIncrementSql, tenantId);
                }
            }
        }
        return true;
    }

    /**
     * 给Oracle表添加字段
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @param fieldList 字段列表
     * @return 是否添加成功
     */
    public Boolean createColumn(String tenantId, String tableName, List<Field> fieldList) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Field field : fieldList) {
            String type = field.getType().getSimpleName();
            String name = field.getName();
            switch (type) {
                case "String":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" varchar2(255);");
                    break;
                case "Integer":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" number(10);");
                    break;
                case "Long":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" number(20);");
                    break;
                case "Date":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" date;");
                    break;
                case "Double":
                case "BigDecimal":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" number(20,2);");
                    break;
                case "Boolean":
                    stringBuffer.append("alter table ").append(tableName).append(" add ").append(name).append(" number(1);");
                    break;
                default:
                    log.error("不支持的类型：" + type);
                    return false;
            }
            //开始执行sql语句
            databaseCore.execute(stringBuffer.toString(), tenantId);
            stringBuffer = new StringBuilder();
        }
        return true;
    }

    /**
     * 删除Oracle表字段
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @param fieldList 字段列表
     * @return 是否删除成功
     */
    public Boolean deleteColumn(String tenantId, String tableName, List<Field> fieldList) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Field field : fieldList) {
            String name = field.getName();
            stringBuffer.append("alter table ").append(tableName).append(" drop column ").append(name).append(";");
            //开始执行sql语句
            databaseCore.execute(stringBuffer.toString(), tenantId);
            stringBuffer = new StringBuilder();
        }
        return true;
    }

    /**
     * 判断表是否存在
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @return true 存在 false 不存在
     */
    public Boolean isTableExist(String tenantId, String tableName) {
        String sql = "select count(*) from user_tables where table_name = " + tableName.toUpperCase();
        Integer count = databaseCore.execute(sql, tenantId, Integer.class);
        return count > 0;
    }

    /**
     * 判断Class的@IndexAop是否存在，如果存在判断索引是否存在，如果不存在就创建索引
     *
     * @param tenantId 租户id
     * @param clazz    类
     */
    public void createIndex(String tenantId, Class<?> clazz) {
        String tableName = entityCore.getEntityName(clazz, tenantId);
        IndexAop annotation = clazz.getAnnotation(IndexAop.class);
        String[] indexs = clazz.getAnnotation(IndexAop.class).value();
        if (annotation != null) {
            for (String index : indexs) {
                String[] indexField = index.split(",");
                String indexName = indexField[0];
                String indexFieldStr = indexField[1];
                String indexType = indexField[2];
                String sql = "select count(*) from user_indexes where index_name = '" + indexName.toUpperCase() + "'";
                Integer count = databaseCore.execute(sql, tenantId, Integer.class);
                if (count == 0) {
                    sql = "create " + indexType + " index " + indexName + " on " + tableName + "(" + indexFieldStr + ")";
                    databaseCore.execute(sql, tenantId);
                }
            }
        }
    }

}
