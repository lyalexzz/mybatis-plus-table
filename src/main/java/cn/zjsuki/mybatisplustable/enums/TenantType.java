package cn.zjsuki.mybatisplustable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 租户级别
 * @author: LiYu
 * @create: 2023-06-07 17:51
 **/
@Getter
@AllArgsConstructor
public enum TenantType {
    /**
     * 不开启多租户
     */
    NONE(1, "不开启多租户"),
    /**
     * 基于列的多租户
     */
    COLUMN(2, "基于列的多租户"),
    /**
     * 基于表名的多租户
     */
    TABLE(3, "基于表名的多租户"),
    /**
     * 基于数据源的多租户
     */
    DATASOURCE(4, "基于数据源的多租户");

    private final Integer value;

    private final String desc;

}
