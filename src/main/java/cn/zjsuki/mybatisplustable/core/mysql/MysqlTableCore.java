package cn.zjsuki.mybatisplustable.core.mysql;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
import cn.zjsuki.mybatisplustable.enums.DataType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: mybatis-plus-table
 * @description: 表主程序
 * @author: LiYu
 * @create: 2023-06-01 12:07
 **/
@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan
public class MysqlTableCore {
    private final MysqlIndexCore mysqlIndexCore;
    private final MysqlEntityCore mysqlEntityCore;
    private final MysqlDatabaseCore databaseCore;

    /**
     * 获取不存在的列
     *
     * @param tenantId    租户id
     * @param tableName   表名
     * @param columnNames 列名
     * @return 结果
     */
    public List<Field> getNotExitColumn(String tenantId, String tableName, List<Field> columnNames) throws SQLException {
        List<Field> notExitColumn = new ArrayList<>();
        try (Connection connection = databaseCore.getConnection(tenantId)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            //获取所有的列名
            List<String> columnList = new ArrayList<>();
            while (resultSet.next()) {
                columnList.add(resultSet.getString("COLUMN_NAME"));
            }

            for (Field desiredColumnName : columnNames) {
                boolean exit = false;
                for (String columnName : columnList) {
                    if (mysqlEntityCore.getFieldName(desiredColumnName).equalsIgnoreCase(columnName)) {
                        exit = true;
                    }
                }
                if (!exit) {
                    notExitColumn.add(desiredColumnName);
                }
            }
            return notExitColumn;
        }
    }

    /**
     * 获取表结构存在的字段但是实体类中不存在的字段
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public List<String> getNotExitField(String tenantId, String tableName, List<Field> fieldList) throws SQLException {
        List<String> notExitField = new ArrayList<>();
        try (Connection connection = databaseCore.getConnection(tenantId)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                boolean exit = false;
                for (Field field : fieldList) {
                    String fieldName = mysqlEntityCore.getFieldName(field);
                    if (fieldName.equalsIgnoreCase(columnName)) {
                        exit = true;
                    }
                }
                if (!exit) {
                    notExitField.add(columnName);
                }
            }
            return notExitField;
        }
    }


    /**
     * 通过List<Field>获取到每个Field的TableField value字段，然后通过Field获取字段doc注释，最后通过传入的tableName和List<Field>创建字段
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public void createColumn(String tenantId, String tableName, List<Field> fieldList) {
        AtomicReference<StringBuffer> stringBuffer = new AtomicReference<>(new StringBuffer());
        fieldList.forEach(val -> {
            String fieldName = mysqlEntityCore.getFieldName(val);
            String fieldType = val.getType().getSimpleName();
            String fieldDoc = val.getAnnotation(TableField.class) != null ? val.getAnnotation(TableField.class).value() : "";
            if (DataType.STRING.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" varchar(255) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.INTEGER.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" int(11) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.LONG.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" bigint(20) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.DOUBLE.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" double COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.FLOAT.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" float COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.DATE.getDesc().equalsIgnoreCase(fieldType) || DataType.LocalDateTime.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" datetime COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.BOOLEAN.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" tinyint(1) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.BIGDECIMAL.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" decimal(19,2) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.BYTE.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" tinyint(4) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.SHORT.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" smallint(6) COMMENT '").append(fieldDoc).append("';");
            } else if (DataType.CHARACTER.getDesc().equalsIgnoreCase(fieldType)) {
                stringBuffer.get().append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(fieldName).append(" char(1) COMMENT '").append(fieldDoc).append("';");
            }
            //开始执行sql语句
            databaseCore.execute(stringBuffer.toString(), tenantId);
            stringBuffer.set(new StringBuffer());
        });

    }

    /**
     * 删除字段
     *
     * @param tenantId  租户id
     * @param tableName 表名称
     * @param fieldList 字段列表
     */
    public void deleteColumn(String tenantId, String tableName, List<String> fieldList) {
        fieldList.forEach(val -> {
            String fieldName = mysqlEntityCore.humpToUnderline(val);
            String deleteSql = "ALTER TABLE " + tableName + " DROP COLUMN " + fieldName + ";";
            log.info("删除字段sql语句：{}", deleteSql);
            databaseCore.execute(deleteSql, tenantId);
        });

    }

    /**
     * 通过Class以及他的mybatis-plus注解创建表
     *
     * @param tenantId  租户id
     * @param clazz     类
     * @param tableName 表名
     * @return 是否创建成功
     */
    public Boolean createTable(String tenantId, Class<?> clazz, String tableName) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("create table ").append(tableName).append("(");
            for (Field field : fields) {
                String fieldName = mysqlEntityCore.getFieldName(field);
                String fieldDoc;
                if (field.getAnnotation(TableField.class) != null) {
                    TableField tableField = field.getAnnotation(TableField.class);
                    if (!tableField.exist()) {
                        continue;
                    }
                    fieldDoc = tableField.value();
                } else {
                    //获取字段的doc注释
                    fieldDoc = "";
                }
                appendSql(field, fieldName, stringBuffer, fieldDoc);
            }
            ;
            String autoIncrementSql = "";
            //获取携带TableId的字段，并且为他添加主键以及根据主键生成策略
            for (Field field : fields) {
                if (field.getAnnotation(TableId.class) != null) {
                    String fieldName = mysqlEntityCore.getFieldName(field);
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equalsIgnoreCase(fieldType)) {
                        stringBuffer.append("primary key (").append(fieldName).append("))");
                    } else if ("Integer".equalsIgnoreCase(fieldType)) {
                        stringBuffer.append("primary key (").append(fieldName).append("))");
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        stringBuffer.append("primary key (").append(fieldName).append("))");
                    }
                    if (field.getAnnotation(TableId.class).type() == IdType.AUTO) {
                        stringBuffer.append(" ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;");
                        //并且为他添加自增
                        autoIncrementSql = "ALTER TABLE " + tableName + " modify " + fieldName + " int(11) AUTO_INCREMENT;";
                    } else {
                        stringBuffer.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                    }
                }
            }
            log.info("TableMaintenanceService.createTable sql:{}", stringBuffer);
            //开始执行sql语句
            databaseCore.execute(stringBuffer.toString(), tenantId);
            if (StringUtils.isNotEmpty(autoIncrementSql)) {
                databaseCore.execute(autoIncrementSql, tenantId);
            }
            if (clazz.getAnnotation(IndexAop.class) != null) {
                String[] indexs = clazz.getAnnotation(IndexAop.class).value();
                for (String index : indexs) {
                    String[] indexField = index.split(",");
                    String indexName = indexField[0];
                    String indexFieldStr = indexField[1];
                    String indexType = indexField[2];
                    mysqlIndexCore.createIndex(tenantId, tableName, indexName, indexFieldStr, indexType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("TableMaintenanceService.createTable error:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据字段类型拼接sql
     *
     * @param field        字段
     * @param fieldName    字段名
     * @param stringBuffer sql语句
     * @param fieldDoc     字段注释
     */
    private void appendSql(Field field, String fieldName, StringBuffer stringBuffer, String fieldDoc) {
        String fieldType = field.getType().getSimpleName();
        if ("String".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" varchar(255) COMMENT '").append(fieldDoc).append("',");
        } else if ("Integer".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" int(11) COMMENT '").append(fieldDoc).append("',");
        } else if ("Long".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" bigint(20) COMMENT '").append(fieldDoc).append("',");
        } else if ("Double".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" double COMMENT '").append(fieldDoc).append("',");
        } else if ("Float".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" float COMMENT '").append(fieldDoc).append("',");
        } else if ("Date".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" datetime COMMENT '").append(fieldDoc).append("',");
        } else if ("Boolean".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" tinyint(1) COMMENT '").append(fieldDoc).append("',");
        } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" decimal(19,2) COMMENT '").append(fieldDoc).append("',");
        } else if ("Byte".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" tinyint(4) COMMENT '").append(fieldDoc).append("',");
        } else if ("Short".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" smallint(6) COMMENT '").append(fieldDoc).append("',");
        } else if ("Character".equalsIgnoreCase(fieldType)) {
            stringBuffer.append(fieldName).append(" char(1) COMMENT '").append(fieldDoc).append("',");
        }
    }

    /**
     * 判断Class的@IndexAop是否存在，如果存在判断索引是否存在，如果不存在就创建索引
     *
     * @param tenantId 租户id
     * @param clazz    类
     */
    public void createIndex(String tenantId, Class<?> clazz) {
        //获取表名
        String tableName = clazz.getAnnotation(TableName.class).value();
        //判断实体类有没有@IndexAop注解，如果有就通过注解的value值来创建索引
        if (clazz.getAnnotation(IndexAop.class) != null) {
            //表中的索引如果在注解中不存在就删除索引，如果存在就不做任何操作
            List<Map<String, Object>> list = mysqlIndexCore.getIndex(tableName);
            String[] indexs = clazz.getAnnotation(IndexAop.class).value();
            for (String index : indexs) {
                String[] indexField = index.split(",");
                String indexName = indexField[0];
                String indexFieldStr = indexField[1];
                String indexType = indexField[2];
                boolean flag = false;
                for (Map<String, Object> map : list) {
                    if (indexName.equals(map.get("Key_name"))) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    mysqlIndexCore.createIndex(tenantId, tableName, indexName, indexFieldStr, indexType);
                }
            }
        }
    }

    /**
     * 判断表是否存在
     *
     * @param tenantId  租户id
     * @param tableName 表名
     * @return true 存在 false 不存在
     */
    public boolean isTableExist(String tenantId, String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.TABLES WHERE table_name = '" + tableName + "'";
        Integer count = databaseCore.execute(sql, tenantId, Integer.class);
        return count != null && count > 0;
    }


}
