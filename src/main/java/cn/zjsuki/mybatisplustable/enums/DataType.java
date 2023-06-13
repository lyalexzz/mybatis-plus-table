package cn.zjsuki.mybatisplustable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 数据类型
 * @author: LiYu
 * @create: 2023-06-13 11:15
 **/
@Getter
@AllArgsConstructor
public enum DataType {
    //java数据类型
    STRING("String", "String"),
    INTEGER("Integer", "Integer"),
    LONG("Long", "Long"),
    DOUBLE("Double", "Double"),
    FLOAT("Float", "Float"),
    DATE("Date", "Date"),
    LocalDateTime("LocalDateTime", "LocalDateTime"),
    BOOLEAN("Boolean", "Boolean"),
    BIGDECIMAL("BigDecimal", "BigDecimal"),
    BYTE("Byte", "Byte"),
    SHORT("Short", "Short"),
    CHARACTER("Character", "Character");

    private final String value;

    private final String desc;
}
