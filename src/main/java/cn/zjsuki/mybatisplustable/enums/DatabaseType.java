package cn.zjsuki.mybatisplustable.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: mybatis-plus-table
 * @description: 数据库类型
 * @author: LiYu
 * @create: 2023-06-01 11:42
 **/
@Getter
@AllArgsConstructor
public enum DatabaseType {
    /**
     * mysql数据库
     */
    MYSQL(1,"MySQl");

    private final Integer value;

    private final String desc;
}
