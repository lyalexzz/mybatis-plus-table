package cn.zjsuki.mybatisplustable.main;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlEntityCore;
import cn.zjsuki.mybatisplustable.core.mysql.MysqlTableCore;
import cn.zjsuki.mybatisplustable.enums.OperationModeType;
import cn.zjsuki.mybatisplustable.utils.MysqlStart;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan
public class TableGeneral implements CommandLineRunner {
    private final MyBatisPlusTableConfig config;
    private static final ThreadPoolExecutor EXECUTOR;
    private final MysqlStart mysqlStart;

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
            if (config.getOperationMode().equals(OperationModeType.AT_RUNTIME)) {
                return;
            }
            switch (config.getDatabaseType()) {
                case MYSQL:
                    mysqlStart.start();
                    break;
                case ORACLE:
                    break;
                case SQLSERVER:
                    break;
                case POSTGRESQL:
                    break;
                default:
                    break;
            }

        };
        EXECUTOR.execute(task);
    }

}
