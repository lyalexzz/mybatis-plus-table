package cn.zjsuki.mybatisplustable.core;

import cn.zjsuki.mybatisplustable.config.MyBatisPlusTableConfig;
import cn.zjsuki.mybatisplustable.enums.TenantFollowType;
import cn.zjsuki.mybatisplustable.enums.TenantType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: mybatis-plus-table
 * @description: 实体类方法
 * @author: LiYu
 * @create: 2023-06-01 11:46
 **/
@RequiredArgsConstructor
@Component
@ComponentScan
public class EntityCore {
    private final MyBatisPlusTableConfig config;

    /**
     * 扫描包
     *
     * @param packageName 包路径
     * @return 结果
     */
    public static List<Class<?>> scanPackageForEntities(String packageName) {
        List<Class<?>> entityClasses = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        File packageDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(packagePath).getFile());
        if (packageDirectory.exists() && packageDirectory.isDirectory()) {
            File[] files = packageDirectory.listFiles();
            assert files != null;
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile() && fileName.endsWith(".class")) {
                    String className = packageName + '.' + fileName.substring(0, fileName.lastIndexOf('.'));
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isEntityClass(clazz)) {
                            entityClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return entityClasses;
    }

    /**
     * 获取实体类的所有字段
     *
     * @param clazz 实体类
     * @return 结果
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        // 遍历当前类的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);
        // 递归遍历父类的字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            List<Field> superFields = getAllFields(superClass);
            fields.addAll(superFields);
        }
        return fields;
    }


    /**
     * 判断是否为需要判断的实体类
     *
     * @param clazz 类
     * @return 是否
     */
    public static boolean isEntityClass(Class<?> clazz) {
        //判断是否有@TableName注解，有的话则放入生成列表
        return clazz.isAnnotationPresent(TableName.class);
    }

    /**
     * 获取字段名称
     *
     * @param field 字段
     * @return 结果
     */
    public String getFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (!tableField.value().isEmpty()) {
                fieldName = tableField.value();
            }
        }
        return humpToUnderline(fieldName);
    }

    /**
     * 获取实体类名称
     *
     * @param clazz    实体类
     * @param tenantId 租户id
     * @return 结果
     */
    public String getEntityName(Class<?> clazz, String tenantId) {
        String entityName = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(TableName.class)) {
            TableName tableName = clazz.getAnnotation(TableName.class);
            if (!tableName.value().isEmpty()) {
                entityName = tableName.value();
            }
        }
        if (config.getTenantType().equals(TenantType.TABLE)) {
            if (config.getTenantFollowType().equals(TenantFollowType.SUFFIX)) {
                entityName = entityName + "_" + tenantId;
            } else {
                entityName = tenantId + "_" + entityName;
            }
        }
        return humpToUnderline(entityName);
    }

    /**
     * 驼峰转下划线
     *
     * @param str 驼峰字符串
     * @return 结果
     */
    public String humpToUnderline(String str) {
        if (!config.hump) {
            return str;
        }
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); ++i) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }


}
