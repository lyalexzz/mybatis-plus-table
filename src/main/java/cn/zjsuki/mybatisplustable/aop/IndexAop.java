package cn.zjsuki.mybatisplustable.aop;

import java.lang.annotation.*;

/**
 * @program: mybatis-plus-table
 * @description: 索引切面
 * @author: LiYu
 * @create: 2023-06-04 13:25
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IndexAop {
    /**
     * 索引合集
     */
    String[] value() default {};
}
