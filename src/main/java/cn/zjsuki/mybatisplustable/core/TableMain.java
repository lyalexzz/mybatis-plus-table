package cn.zjsuki.mybatisplustable.core;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: mybatis-plus-table
 * @description: 表主程序
 * @author: LiYu
 * @create: 2023-06-01 12:07
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class TableMain {
    private final JdbcTemplate jdbcTemplate;

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
            for (Field desiredColumnName : columnNames) {
                boolean exit = false;
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (desiredColumnName.getName().equalsIgnoreCase(columnName)) {
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
     * 获取表结构不存在的字段但是实体类中存在的字段
     *
     * @param tableName 表名
     * @param fieldList 字段列表
     */
    public List<Field> getNotExitTableColumn(String tableName, List<Field> fieldList) {
        List<Field> notExitColumn = new ArrayList<>();
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            for (Field desiredColumnName : fieldList) {
                boolean exit = false;
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (desiredColumnName.getAnnotation(TableField.class).value().equalsIgnoreCase(columnName)) {
                        exit = true;
                    }
                }
                if (!exit) {
                    notExitColumn.add(desiredColumnName);
                }
            }
            return notExitColumn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
            //通过val.gatewayTableField().value()获取到字段名 并且组装添加字段的sql语句 通过字段类型来生成不同的sql语句
            TableField tableField = val.getAnnotation(TableField.class);
            String fieldName = tableField.value();
            String fieldType = val.getType().getSimpleName();
            String fieldDoc = val.getAnnotation(TableField.class).value();
            if (fieldType.equalsIgnoreCase("String")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" varchar(255) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Integer")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" int(11) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Long")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" bigint(20) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Double")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" double comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Float")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" float comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Date")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" datetime comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Boolean")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" tinyint(1) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("BigDecimal")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" decimal(19,2) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Byte")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" tinyint(4) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Short")) {
                stringBuffer.append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" smallint(6) comment '").append(fieldDoc).append("';");
            } else if (fieldType.equalsIgnoreCase("Character")) {
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
    public void deleteColumn(String tableName, List<Field> fieldList) {
        StringBuffer stringBuffer = new StringBuffer();
        fieldList.forEach(val -> {
            //通过val.gatewayTableField().value()获取到字段名 并且组装添加字段的sql语句 通过字段类型来生成不同的sql语句
            TableField tableField = val.getAnnotation(TableField.class);
            String fieldName = tableField.value();
            stringBuffer.append("alter table ").append(tableName).append(" drop column ").append(fieldName).append(";");
        });
        //开始执行sql语句
        jdbcTemplate.execute(stringBuffer.toString());
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
            if (field.getName().equals("serialVersionUID")) {
                continue;
            }
            //获取字段名
            String fieldName = field.getName();
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
            if (fieldType.equalsIgnoreCase("String")) {
                stringBuffer.append(fieldName).append(" varchar(255) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Integer")) {
                stringBuffer.append(fieldName).append(" int(11) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Long")) {
                stringBuffer.append(fieldName).append(" bigint(20) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Double")) {
                stringBuffer.append(fieldName).append(" double comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Float")) {
                stringBuffer.append(fieldName).append(" float comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Date")) {
                stringBuffer.append(fieldName).append(" datetime comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Boolean")) {
                stringBuffer.append(fieldName).append(" tinyint(1) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("BigDecimal")) {
                stringBuffer.append(fieldName).append(" decimal(19,2) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Byte")) {
                stringBuffer.append(fieldName).append(" tinyint(4) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Short")) {
                stringBuffer.append(fieldName).append(" smallint(6) comment '").append(fieldDoc).append("',");
            } else if (fieldType.equalsIgnoreCase("Character")) {
                stringBuffer.append(fieldName).append(" char(1) comment '").append(fieldDoc).append("',");
            }
        }
        String autoIncrementSql = "";
        //获取携带TableId的字段，并且为他添加主键以及根据主键生成策略
        for (Field field : fields) {
            if (field.getAnnotation(TableId.class) != null) {
                String fieldName = field.getName();
                String fieldType = field.getType().getSimpleName();
                if (fieldType.equalsIgnoreCase("String")) {
                    stringBuffer.append("primary key (").append(fieldName).append("))");
                } else if (fieldType.equalsIgnoreCase("Integer")) {
                    stringBuffer.append("primary key (").append(fieldName).append("))");
                } else if (fieldType.equalsIgnoreCase("Long")) {
                    stringBuffer.append("primary key (").append(fieldName).append("))");
                }
                if (field.getAnnotation(TableId.class).type() == IdType.AUTO) {
                    stringBuffer.append(" ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;");
                    //并且为他添加自增
                    autoIncrementSql = "ALTER TABLE" + tableName + " modify " + fieldName + " int(11) AUTO_INCREMENT;";
                } else {
                    stringBuffer.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                }
            }
        }
        log.info("TableMaintenanceService.createTable sql:{}", stringBuffer.toString());
        //开始执行sql语句
        jdbcTemplate.execute(stringBuffer.toString());
        if (StringUtils.isNotEmpty(autoIncrementSql)) {
            jdbcTemplate.execute(autoIncrementSql);
        }
    }

}
