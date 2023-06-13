package cn.zjsuki.mybatisplustable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 租户ID跟随
 * @author: LiYu
 * @create: 2023-06-13 13:05
 **/
@Getter
@AllArgsConstructor
public enum TenantFollowType {
    /**
     * table_name_suffix
     */
    SUFFIX("suffix", "后缀"),
    /**
     * prefix_table_name
     */
    PREFIX("prefix", "前缀");

    private final String value;

    private final String desc;
}
