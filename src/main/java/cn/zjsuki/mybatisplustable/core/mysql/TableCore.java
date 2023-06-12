package cn.zjsuki.mybatisplustable.core.mysql;

import cn.zjsuki.mybatisplustable.aop.IndexAop;
import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class TableCore {
    private final JdbcTemplate jdbcTemplate;
    private final IndexCore indexCore;
    private final EntityCore entityCore;

    /**
     * 获取不存在的列
     *
     * @param tableName   表名
     * @param columnNames 列名
     * @return 结果
     * @throws SQLException
     */
    public List<Field> getNotExitColumn(String tableName, List<Field> columnNames) throws SQLException {
        List<Field> notExitColumn = new ArrayList<>();
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
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
                    if (entityCore.getFieldName(desiredColumnName).equalsIgnoreCase(columnName)) {
                        exit = true;
                    }
                }
                log.info("是否存在：{}", exit);
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
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public List<String> getNotExitField(String tableName, List<Field> fieldList) throws SQLException {
        List<String> notExitField = new ArrayList<>();
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                boolean exit = false;
                for (Field field : fieldList) {
                    String fieldName = entityCore.getFieldName(field);
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
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public void createColumn(String tableName, List<Field> fieldList) {
        StringBuffer stringBuffer = new StringBuffer();
        fieldList.forEach(val -> {
            String fieldName = entityCore.getFieldName(val);
            String fieldType = val.getType().getSimpleName();
            String fieldDoc = val.getAnnotation(TableField.class) != null ? val.getAnnotation(TableField.class).value() : "";
            if ("String".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" varchar(255) comment '").append(fieldDoc).append("';");
            } else if ("Integer".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" int(11) comment '").append(fieldDoc).append("';");
            } else if ("Long".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" bigint(20) comment '").append(fieldDoc).append("';");
            } else if ("Double".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" double comment '").append(fieldDoc).append("';");
            } else if ("Float".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" float comment '").append(fieldDoc).append("';");
            } else if ("Date".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" datetime comment '").append(fieldDoc).append("';");
            } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" tinyint(1) comment '").append(fieldDoc).append("';");
            } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" decimal(19,2) comment '").append(fieldDoc).append("';");
            } else if ("Byte".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" tinyint(4) comment '").append(fieldDoc).append("';");
            } else if ("Short".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" smallint(6) comment '").append(fieldDoc).append("';");
            } else if ("Character".equalsIgnoreCase(fieldType)) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" char(1) comment '").append(fieldDoc).append("';");
            }
        });
        //开始执行sql语句
        jdbcTemplate.execute(stringBuffer.toString());
    }

    /**
     * 删除字段
     *
     * @param tableName 表名称
     * @param fieldList 字段列表
     */
    public void deleteColumn(String tableName, List<String> fieldList) {
        final StringBuffer[] stringBuffer = {new StringBuffer()};
        fieldList.forEach(val -> {
            String fieldName = entityCore.humpToUnderline(val);
            stringBuffer[0].append("alter table ").append(tableName).append(" drop column ").append(fieldName).append(";");
            jdbcTemplate.execute(stringBuffer[0].toString());
            stringBuffer[0] = new StringBuffer();
        });

    }

    /**
     * 通过Class以及他的mybatis-plus注解创建表
     *
     * @param clazz 类
     */
    public void createTable(Class<?> clazz) {
        //获取表名
        String tableName = clazz.getAnnotation(TableName.class).value();
        //获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        //拼接sql语句
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("create table ").append(tableName).append("(");
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            //获取字段名
            String fieldName = entityCore.getFieldName(field);
            //获取字段类型
            String fieldType = field.getType().getSimpleName();
            //获取字段注释
            String fieldDoc;
            if (field.getAnnotation(TableField.class) != null) {
                fieldDoc = field.getAnnotation(TableField.class).value();
            } else {
                //获取字段的doc注释
                fieldDoc = "";
            }
            if ("String".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" varchar(255) comment '").append(fieldDoc).append("',");
            } else if ("Integer".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" int(11) comment '").append(fieldDoc).append("',");
            } else if ("Long".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" bigint(20) comment '").append(fieldDoc).append("',");
            } else if ("Double".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" double comment '").append(fieldDoc).append("',");
            } else if ("Float".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" float comment '").append(fieldDoc).append("',");
            } else if ("Date".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" datetime comment '").append(fieldDoc).append("',");
            } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" tinyint(1) comment '").append(fieldDoc).append("',");
            } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" decimal(19,2) comment '").append(fieldDoc).append("',");
            } else if ("Byte".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" tinyint(4) comment '").append(fieldDoc).append("',");
            } else if ("Short".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" smallint(6) comment '").append(fieldDoc).append("',");
            } else if ("Character".equalsIgnoreCase(fieldType)) {
                stringBuffer.append(fieldName).append(" char(1) comment '").append(fieldDoc).append("',");
            }
        }
        String autoIncrementSql = "";
        //获取携带TableId的字段，并且为他添加主键以及根据主键生成策略
        for (Field field : fields) {
            if (field.getAnnotation(TableId.class) != null) {
                String fieldName = entityCore.getFieldName(field);
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
        jdbcTemplate.execute(stringBuffer.toString());
        if (StringUtils.isNotEmpty(autoIncrementSql)) {
            jdbcTemplate.execute(autoIncrementSql);
        }
        //判断实体类有没有@IndexAop注解，如果有就通过注解的value值来创建索引
        if (clazz.getAnnotation(IndexAop.class) != null) {
            String[] indexs = clazz.getAnnotation(IndexAop.class).value();
            for (String index : indexs) {
                String[] indexField = index.split(",");
                String indexName = indexField[0];
                String indexFieldStr = indexField[1];
                String indexType = indexField[2];
                indexCore.createIndex(tableName, indexName, indexFieldStr, indexType);
            }
        }
    }

    /**
     * 判断Class的@IndexAop是否存在，如果存在判断索引是否存在，如果不存在就创建索引
     *
     * @param clazz 类
     */
    public void createIndex(Class<?> clazz) {
        //获取表名
        String tableName = clazz.getAnnotation(TableName.class).value();
        //判断实体类有没有@IndexAop注解，如果有就通过注解的value值来创建索引
        if (clazz.getAnnotation(IndexAop.class) != null) {
            //表中的索引如果在注解中不存在就删除索引，如果存在就不做任何操作
            List<Map<String, Object>> list = indexCore.getIndex(tableName);
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
                    indexCore.createIndex(tableName, indexName, indexFieldStr, indexType);
                }
            }
        }
    }

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return true 存在 false 不存在
     */
    public boolean isTableExist(String tableName) {
        String sql = "select count(*) from information_schema.TABLES where table_name = '" + tableName + "'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }


}
