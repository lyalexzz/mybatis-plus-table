package cn.zjsuki.mybatisplustable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 运行方式
 * @author: LiYu
 * @create: 2023-06-13 11:22
 **/
@Getter
@AllArgsConstructor
public enum OperationModeType {
    /**
     * 程序启动时加载表
     */
    AT_STARTUP("atStartup", "启动时"),
    /**
     * 自行调用加载表
     */
    AT_RUNTIME("atRuntime", "运行时");

    private final String value;

    private final String desc;
}
