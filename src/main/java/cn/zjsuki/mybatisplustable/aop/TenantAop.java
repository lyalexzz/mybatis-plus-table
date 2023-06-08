package cn.zjsuki.mybatisplustable.aop;

import java.lang.annotation.*;

/**
 * @program: mybatis-plus-table
 * @description: 多租户
 * @author: LiYu
 * @create: 2023-06-04 13:25
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantAop {
    /**
     * 租户字段
     */
    String value() default "";
}
