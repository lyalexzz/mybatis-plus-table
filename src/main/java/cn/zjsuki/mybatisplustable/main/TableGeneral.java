package cn.zjsuki.mybatisplustable.main;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.core.mysql.EntityCore;
import cn.zjsuki.mybatisplustable.core.mysql.TableCore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @program: mybatis-plus-table
 * @description: 表生成
 * @author: LiYu
 * @create: 2023-06-01 11:54
 **/
@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class TableGeneral implements CommandLineRunner {
    private final MyBatisPlusTableConfig config;
    private final TableCore tableCore;
    private static final ThreadPoolExecutor EXECUTOR;

    static {
        // 核心线程数
        int corePoolSize = 5;
        // 最大线程数
        int maximumPoolSize = 10;
        // 线程空闲时间
        long keepAliveTime = 60;
        // 时间单位
        TimeUnit unit = TimeUnit.SECONDS;
        // 使用无界队列作为任务队列
        EXECUTOR = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>());
    }

    @Override
    public void run(String... args) {
        Runnable task = () -> {
            //判断是否启用插件
            if (!config.getEnable()) {
                return;
            }
            //开始扫描表
            List<Class<?>> entityList = EntityCore.scanPackageForEntities(config.getEntityScan());
            for (Class<?> clazz : entityList) {
                //判断表是否存在
                TableName tableName = clazz.getAnnotation(TableName.class);
                if (tableExit(tableName.value())) {
                    //获取实体类的所有字段
                    List<Field> fieldList = EntityCore.getAllFields(clazz);
                    List<Field> fieldName = new ArrayList<>();
                    fieldList.forEach(val -> {
                        TableField tableField = val.getAnnotation(TableField.class);
                        if (tableField != null && tableField.exist()) {
                            fieldName.add(val);
                        }
                    });
                    //判断实体类里面的字段是否都存在
                    List<Field> getNotExitColumn = null;
                    try {
                        getNotExitColumn = tableCore.getNotExitColumn(tableName.value(), fieldName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (getNotExitColumn.size() > 0) {
                        //开始创建字段
                        tableCore.createColumn(tableName.value(), getNotExitColumn);
                    }
                    List<Field> getNotExitTable = tableCore.getNotExitTableColumn(tableName.value(), fieldName);
                    if (getNotExitTable.size() > 0) {
                        //开始创建字段
                        tableCore.deleteColumn(tableName.value(), getNotExitTable);
                    }
                    //创建索引
                    tableCore.createIndex(clazz);
                } else {
                    log.info("表{}不存在，开始创建表", tableName.value());
                    tableCore.createTable(clazz);
                }
            }
        };
        EXECUTOR.execute(task);
    }


    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return 结果
     */
    public boolean tableExit(String tableName) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        return tableInfo != null;
    }
}
