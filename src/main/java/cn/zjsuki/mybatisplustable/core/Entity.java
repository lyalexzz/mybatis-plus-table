package cn.zjsuki.mybatisplustable.core;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: mybatis-plus-table
 * @description: 实体类扫描
 * @author: LiYu
 * @create: 2023-06-01 11:46
 **/
public class Entity {
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
}
