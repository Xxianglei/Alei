
> 微信公众号：**Java编程之道** 

> Spring Data全家桶中对Elasticsearch也做了集成，本文接下来会基于最新的Spring Data Elasticsearch 4.0介绍在spring中对ES的基本使用。

- 本文分为上下两部分，分别为
  - 简易版的Spring Boot JPA形式的使用
  - 进阶版ES原生API的使用

# 前言

## 1. 新版本特性

既然是基于最新的Spring Data Elasticsearch 4.0来学习，那么首先得弄清楚4.0具有哪些特性，贴上官方文档
![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NPclNCYlo2d2ljWVcwSnZDT3dnOGZ6WmZIUE5nSWliYkU3RnVYV3p6MEFPTE1qMTFwZnFiRDNTQS8w?x-oss-process=image/format,png)      从图中可以看到，4.0对应支持ES版本为7.6.2，并且弃用了对**TransportClient**的使用，实际上ES从7.x版本开始就弃用了对**TransportClient**的使用，并将会在8.0版本开始完全删除**TransportClient**，这意味着后续我们在使用客户端连接ES时得和9300告别了。
**详细的新版本特性请参考官方文档(https://sohu.gg/X3lyLU)**
## 2. 环境搭建
   - ES7.6.2及以上版本的安装，如果还没安装，可以参考之前的安装文章。
   - JDK请确认1.8及以上版本
   - 项目初始化：使用spring的快速开始功能即可(https://start.spring.io/)，记得勾选图中插件哦。
   ![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NUaGZYbGVpYUdXYjRpYWU0Z1UzQ0FsazJySmtoZEpWMkN1MWpMbE03VUN0MHhWMDA2aDZwaWNvMVEvMA?x-oss-process=image/format,png)

# 简易版的JPA形式使用
## 1.配置ES连接信息
```yml
##在application.properties配置连接地址，IP自行更换
spring.elasticsearch.rest.uris=http://127.0.0.1:9200
##如果启动配置文件时application.yml，则相应配置为
spring:
  elasticsearch:
    rest:
      uris:
        - http://127.0.0.1:9200
```
```xml
##小提示：spring默认安装的是7.6.2版本的ES，若你安装的是7.6.2以上的版本，可在pom中的<properties>标签中手动替换ES的版本
<elasticsearch.version>7.7.1</elasticsearch.version>
```
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2M3czdyaWFKc3VhVUxZSlBzcHU5ODlxOTc1VHNSVUdxREFSb0VvMzc3dFE5MGU2M0IwUFp3eDJ3LzA?x-oss-process=image/format,png)
## 2.创建DAO层
对ES操作的DAO类是通过继承Spring已经封装好对ES基本的CRUD及分页操作的ElasticsearchRepository类并进行扩展操作接口来实现。
### a.创建Document实体类
学习了Elasticsearch基本概念的同学都知道ES中的存储结构有Index、Document，那在Spring中我们如何实现及定义呢，别担心，Spring已为我们提供了@Document注解，使用方式如下：
```java
/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p></p>
 *
 * @author yingKang
 * @date 2020-06-05 17:57
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "demo-use-log")
public class UseLogDO {
    //在@Document注解中我们还可以指定分片数、副本数等Index相关setting属性；
    //Index的mapping配置则可在字段上添加注解@Field指定属性，若没配置Spring会根据字段属性自动生成
    //指定Document的主键
    @Id
    private String id;

    private Integer sortNo;

    private String result;

    private Date createTime;
}
```
### b.创建Repository
```java
/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p></p>
 *
 * @author yingKang
 * @date 2020-06-05 17:56
 */
public interface UseLogRepository extends ElasticsearchRepository<UseLogDO, String> {
}
```
> 至此我们的DAO层就创建好了，是不是及其简单，ElasticsearchRepository已经封装好了对ES基本的CRUD及分页操作。我们可以在**UseLogRepository**中根据JPA规范自定义所需的方法，且IDEA也有友好的提示

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NIQnZybkxqNmVsUmhyQmo2NVZIQmI0MXR0eU45aWJHaDdkVUpoTllVcVppY3NZYWdRZ2hyV3RpYlEvMA?x-oss-process=image/format,png)
## 3.demo测试CRUD
> **小提示**：spring会在我们启动项目时自动去创建index，若不需要，可在@Document的createIndex设置为false

对Document的保存，ElasticsearchRepository提供了单个的 **save()** 方法和批量保存的 **saveAll()** 方法。示例如下：
```java
    /**
     * 批量保存
     */
    @Test
    void testAdd() {
        List<UseLogDO> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            UseLogDO useLogDO = UseLogDO.builder().id(String.valueOf(i)).sortNo(i).result(String.format("我是%d号", i)).createTime(new Date()).build();
            list.add(useLogDO);
        }
        useLogRepository.saveAll(list);
    }
```
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2M2Vm1mOVQwS0hKaWJ4ZURwV1FzNFcyR3BOZGF6dHYxTlBOWDR4VFRjWEp0cnFST3NpYktXcDZjQS8w?x-oss-process=image/format,png)
***
ElasticsearchRepository仅提供了deleteById的条件删除，我们可通过在自己的Repository中扩展删除接口，如示例中的deleteBySortNoIsGreaterThan(int deleteStartNo)方法，它可删除 **sortNo** 大于参数的Document记录
```java
    /**
     * 删
     */
    @Test
    void testDelete() {
        long deleteNumber = useLogRepository.deleteBySortNoIsGreaterThan(50);
        System.out.println("删除日志数量为：" + deleteNumber);
    }
```
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NqejJVaWNxUWxzQVNyWWxBVGJ6d2dYMkpaZ3VYalNLSU9OdjhpY2p6bnZpYVpabjhvMHg4STJhcXcvMA?x-oss-process=image/format,png)
***
在ElasticsearchRepository的修改功能仅能通过整个Document替换来完成修改操作。但在实际使用中我们常常会只修改其中一部分内容，这种的实现会在下篇的进阶API操作中讲解。
```java
    /**
     * 改
     * 此修改的实现实质是根据ID全覆盖
     */
    @Test
    void testUpdate() {
        Optional<UseLogDO> optional = useLogRepository.findById("1");
        if (optional.isPresent()) {
            UseLogDO useLogDO = optional.get();
            System.out.println(useLogDO.toString());
            System.out.println("-----------------------------------");
            useLogDO.setResult("我是更新后的1号");
            useLogRepository.save(useLogDO);
            System.out.println(useLogRepository.findById("1").toString());
        }
    }
```
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NGdXpMSHRDRkQ3TURGN2hXQjZINHhidHhEZ1UwYkxMdkt3cEpaUUNuY2J1T0RraWN6RW84SnF3LzA?x-oss-process=image/format,png)
***
查询往往是我们使用最频繁的功能，ElasticsearchRepository支持常用的 等于、不等于、大于、小于、早于、晚于、介于、排序、在...中等条件扩展，已基本能满足使用。
示例中扩展的查询接口 **findBySortNoIsBetween(int start, int end, Pageable page)** 的功能为：检索sortNo位于(start,end)之间的Document并支持分页返回。
```java
    /**
     * 分页条件查询
     */
    @Test
    void testQuery(){
        Pageable page = PageRequest.of(0, 5);
        Page<UseLogDO> useLogDOPage = useLogRepository.findBySortNoIsBetween(20, 30, page);
        useLogDOPage.getContent().forEach(useLogDO -> {
            System.out.println(useLogDO.toString());
        });
    }
```
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoN3c2ZlJvM3dQUmlhb29NVXVuZVJWR2NSQUJwRmxjeVlmMkZScE43RlF1c25xS3JCVVdxRkxneTJzaWFYN2U1cmNkbmsyczB0OEFHdUhnLzA?x-oss-process=image/format,png)

> 简易版的JPA形式使用至此就介绍完了，我们已学会了基本的CRUD操作，且无需多余代码。但是在有些业务场景中，往往需要复杂的聚合查询及高亮查询等，这些需要利用原生API来实现，关于**进阶版ES原生API的使用**请移步下篇文章
