<h4 align="right"><strong>简体中文</strong> | <a href="./README_en.md">English</a></h4>

# MyBatis-Pager： 一个轻量且优雅的 MyBatis 分页组件

<p align="center">
    <img src="https://img.shields.io/maven-central/v/com.mybatis-flex/parent?label=Maven%20Central" alt="Maven" />
    <img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
    <br />
    <img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
    <img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
    <img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
    <img src="https://img.shields.io/badge/SpringBoot-v2.x-blue">
    <img src="https://img.shields.io/badge/SpringBoot-v3.x-blue">
</p>

## 优点
- **无侵入**：仅需在`mapper`层接口中增加`Pager<T>`参数即可。
- **零配置**：无需额外增加配置项，能够自动推断数据库分页方言。

## 使用方法
**SpringBoot2 项目引入maven依赖**
```xml
<dependency>
    <groupId>cn.codest</groupId>
    <artifactId>mybatis-pager-spring-boot2-starter</artifactId>
    <version>${mybatis.pager.version}</version>
</dependency>
```

**SpringBoot3 项目引入maven依赖**
```xml
<dependency>
    <groupId>cn.codest</groupId>
    <artifactId>mybatis-pager-spring-boot3-starter</artifactId>
    <version>${mybatis.pager.version}</version>
</dependency>
```

**在`Mapper XML`中定义查询SQL语句**
```xml
<select id="selectPage" resultType="cn.codest.mybatispagerspringboot2test.model.User">
    select
        id,
        name,
        mobile
    from t_user
    <if test="null != name and '' != name">
        where name like concat(#{name}, '%')
    </if>
</select>
```

**定义`Mapper`接口，参数中需要包含Pager分页查询对象**
```java
public interface UserMapper {

    List<User> selectPage(@Param("name") String name, Pager<User> pager);

}
```

**调用`Mapper`接口，查询分页数据**
```java
Pager<User> pager = Pager.of(1);
userMapper.selectPage(null, pager);
log.info("result = [{}]", pager);
```
带条件查询
```java
Pager<User> pager = Pager.of(1);
userMapper.selectPage("郭", pager);
log.info("result = [{}]", pager);
```

## 支持的数据库
目前`jDialects`自带支持以下83种数据库方言
- AccessDialect
- Cache71Dialect
- CobolDialect
- CUBRIDDialect
- DamengDialect
- DataDirectOracle9Dialect
- DB2390Dialect
- DB2390V8Dialect
- DB2400Dialect
- DB297Dialect
- DB2Dialect
- DbfDialect
- DerbyDialect
- DerbyTenFiveDialect
- DerbyTenSevenDialect
- DerbyTenSixDialect
- ExcelDialect
- FirebirdDialect
- FrontBaseDialect
- GBaseDialect
- H2Dialect
- HANAColumnStoreDialect
- HANARowStoreDialect
- HSQLDialect
- Informix10Dialect
- InformixDialect
- Ingres10Dialect
- Ingres9Dialect
- IngresDialect
- InterbaseDialect
- JDataStoreDialect
- MariaDB102Dialect
- MariaDB103Dialect
- MariaDB10Dialect
- MariaDB53Dialect
- MariaDBDialect
- MckoiDialect
- MimerSQLDialect
- MySQL55Dialect
- MySQL57Dialect
- MySQL57InnoDBDialect
- MySQL5Dialect
- MySQL5InnoDBDialect
- MySQL8Dialect
- MySQLDialect
- MySQLInnoDBDialect
- MySQLMyISAMDialect
- Oracle10gDialect
- Oracle12cDialect
- Oracle8iDialect
- Oracle9Dialect
- Oracle9iDialect
- OracleDialect
- ParadoxDialect
- PointbaseDialect
- PostgresPlusDialect
- PostgreSQL81Dialect
- PostgreSQL82Dialect
- PostgreSQL91Dialect
- PostgreSQL92Dialect
- PostgreSQL93Dialect
- PostgreSQL94Dialect
- PostgreSQL95Dialect
- PostgreSQL9Dialect
- PostgreSQLDialect
- ProgressDialect
- RDMSOS2200Dialect
- SAPDBDialect
- SQLiteDialect
- SQLServer2005Dialect
- SQLServer2008Dialect
- SQLServer2012Dialect
- SQLServerDialect
- Sybase11Dialect
- SybaseAnywhereDialect
- SybaseASE157Dialect
- SybaseASE15Dialect
- SybaseDialect
- Teradata14Dialect
- TeradataDialect
- TextDialect
- TimesTenDialect
- XMLDialect