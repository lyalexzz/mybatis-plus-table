# mybatis-plus-table

## 简介

这是基于mybatis-plus注解开发的反逆向工程插件,可根据实体类里面的注解生成表结构，表字段，添加字段，删除字段。并且提供@IndexAop注解，可以动态添加索引

## 使用说明

- 配置文件说明

    ```yaml
    mybatis-plus-table-config:
        enable: true #是否开启自动建表
        entityScan: com.xxx.xxx.entity #实体类扫描路径
        databaseType: mysql #数据库类型
        operationModeType: AT_STARTUP #操作方式 AT_STARTUP程序启动时加载表 AT_RUNTIME自行调用加载表
        hump: true #是否开启驼峰命名
        tenantType: NONE #多租户类型 NONE:不启用 TABLE:表级别，DATASOURCE:数据源级别 默认不启用
        tenantIdList: [1,2,3]    #多租户id列表
        tenantFollowType: SUFFIX #租户ID跟随 SUFFIX后缀 PREFIX前缀
        databaseList: #当tenantType为DATASOURCE时需要配置各个租户的数据源
            租户1:
                url: jdbc:mysql://${MYSQL_ADDR:192.168.2.115}:${MYSQL_PORT:3306}/risk_system?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
                username: ${MYSQL_USER:root}
                password: ${MYSQL_PASSWORD:root}
    ```

- 用到的注解

  @TabelName 当你的实体类有该注解，创建的表名默认为该实体类的value，否则表名就是你的实体类名，当配置文件hump为true时，则表名是你的实体类转下划线的命名，如 UserEntity时，

  创建的表名为user_entity

  @TableId 主键ID需要添加该注解，会自动根据该注解添加主键策略

  @TableField 字段上如果添加了该注解，则默认生成的表字段为他的value值，否则根据实体类名生成表字段，当某个字段不想生成表字段时，则添加@TableField(exist = false)

  @IndexAop 该注解应添加到实体类上面@IndexAop(value = {”indexName,indexFieldStr,indexType”})

- 自行调用

  当operationModeType为AT_RUNTIME时，自启生成表失效，需要自己调用jar包中提供的MyBatisPlusTableService接口