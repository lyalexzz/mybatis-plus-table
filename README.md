# mybatis-plus-table
通过mybatis-plus的注解自动生成表结构 添加或者删除表字段
@IndexAop添加自定义索引，举例：@IndexAop(indexName = "idx_user_name",indexType = "BTREE",indexColumn = "name")
新增多租户功能，配置文件中添加如下配置
mybatis-plus-table-config:
    enable: true #是否开启自动建表
    entityScan：com.xxx.xxx.entity #实体类扫描路径
    databaseType: mysql #数据库类型
    hump: true #是否开启驼峰命名
    tenantIdList: [1,2,3]    #多租户id列表
    tenantType: TABLE #多租户类型 TABLE:表级别，COLUMN:列级别
    databaseList:
        租户1:
            url: jdbc:mysql://${MYSQL_ADDR:192.168.2.115}:${MYSQL_PORT:3306}/risk_system?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
            username: ${MYSQL_USER:root}
            password: ${MYSQL_PASSWORD:root}