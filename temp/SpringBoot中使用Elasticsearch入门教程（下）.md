> 书接上文，学习了**JPA形式的使用**，咱们今天来看看**进阶版ES原生API的使用**有何不同

# 进阶版ES原生API的使用
## 1. 原理概述
如果有小伙伴去跟过`ElasticsearchRepository`所封装操作方法的源码的话，会发现底层其实是通过`ElasticsearchRestTemplate`实现的，再继续跟下去，实质是通过`RestHighLevelClient`的API来操作ES
![ElasticsearchRestTemplate#search源码](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6xGorrLdlByM6hia4YguvGFsM2lTzXXEuc1Jeticibicux6NPszyrsu0JD0xPzD9jOPCJ5nz6LmLXONg/0?wx_fmt=png)
由此我们引出了今天的主角:`RestHighLevelClient`和`ElasticsearchRestTemplate`。他们分别是ES的默认客户端和对客户端一些公共条件构造封装的模板类。我们知道越底层的代码自由度越高，由于上篇中所封装的Repository不能满足所有场景，所以对于特殊场景的实现需要直接去操作底层的API来实现，这也是我们学习下篇的原因之一。
## 2. 学习目标
- Elasticsearch的DSL语法掌握
- 熟悉spring boot中的条件构造类,能达到基础DSL语法效果
- 尝试聚合索引实现

## 3. Start正文

### a.环境搭建及相关配置信息
   同上篇一致，可参看**SpringBoot中使用Elasticsearch入门教程（上）**

### b.Elasticsearch的DSL语法学习
如果已学习过可跳过此节。
如果还未学习过，可先学习DSL语法，再结合本文案例理解更深。
由于ES官方对DSL语法已有很详细的文档，此处直接贴上官网链接：
> **英文版**，可选择不同ES版本：https://www.elastic.co/guide/en/elasticsearch/reference/7.8/query-dsl.html
**中文版**，基于ES 2.x 版本，有些内容可能已经过时：https://www.elastic.co/guide/cn/elasticsearch/guide/current/search-in-depth.html

### c.代码示例

- 1. 创建Document对象
```java
/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p></p>
 *
 * @author yingKang
 * @date 2020年6月27日
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "demo-use-log2")
public class UseLogTwoDO {

    @Id
    private String id;

    private Integer sortNo;

    @Field(type = FieldType.Keyword)
    private String result;

    private Date createTime;
}
```
- 2. demo测试基础DSL语法

首先我们要注入`RestHighLevelClient`和`ElasticsearchRestTemplate`类
```java
    /**
     * {@link ElasticsearchRestTemplate} 本质是基于 {@link RestHighLevelClient} 进行封装的一个template，
     * 让我们在使用highLevelClient时减少了一些公共条件构造的冗余代码，操作ES时代码更加简洁明了
     */
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    /**
     * Java高级Rest客户端是spring提供的默认客户端，低级客户端已被标记过时
     */
    @Autowired
    private RestHighLevelClient highLevelClient;
```
  - 创建索引实现
  
  示例`testCreateIndex1()`中，通过`ElasticsearchRestTemplate`来创建了一个`IndexOperations`（是从4.0版本开始单独封装对Index的操作类）对象，整体代码简洁；而在`testCreateIndex2()`中，则需要显示地指定IndexName和setting设置。两者实现的功能一致，但在代码整洁度上却相差较多。
```java
    /**
     * template创建索引
     * 通过参看create源码，可以看到底层对{@link RestHighLevelClient}的调用，
     * 其实同{@link EsSecondDemoTest#testCreateIndex2()}方法一致
     *
     */
    @Test
    void testCreateIndex1() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(UseLogTwoDO.class);
        if (!indexOperations.exists()) {
            indexOperations.create();
            System.out.println("Create successfully!");
        }else {
            System.out.println("Index has been created!");
        }
    }

    /**
     * client创建索引
     */
    @Test
    void testCreateIndex2() {
        try {
            String indexName = "demo-use-log2";
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            createIndexRequest.settings(getSettings());
            if (!highLevelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)) {
                CreateIndexResponse createIndexResponse = highLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                if (createIndexResponse.isAcknowledged()) {
                    System.out.println("Create successfully!");
                }
            }else {
                System.out.println("Index has been created!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static XContentBuilder getSettings() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        builder.field("index").startObject()
                .field("number_of_shards", 1)
                .field("number_of_replicas", 0)
                .field("refresh_interval", "30s").endObject();
        builder.endObject();
        return builder;
    }
```
  - 新增Document
```java
    /**
     * 批量添加
     */
    @Test
    void testAdd() {
        List<IndexQuery> list = new ArrayList<>(100);
        for (int i = 1; i <= 100; i++) {
            IndexQuery indexQuery = new IndexQuery();
            UseLogTwoDO useLogDO = UseLogTwoDO.builder().id(String.valueOf(i)).sortNo(i).result(String.format("我是%d号", i)).createTime(new Date()).build();
            indexQuery.setId(useLogDO.getId());
            indexQuery.setObject(useLogDO);
            list.add(indexQuery);
        }
        List<String> strings = elasticsearchRestTemplate.bulkIndex(list, elasticsearchRestTemplate.getIndexCoordinatesFor(UseLogTwoDO.class));
        System.out.println(strings);
    }
```
  - 更新Document
  
  在**上篇**中，我们所实现的更新操作实质是通过覆盖整个Document来完成了，针对我们不需要修改的字段，还需重新赋相同的值，这在实际使用过程中，显然是非人化的。但其实`RestHighLevelClient`有提供针对Document中单一属性进行修改的方法，要达到这个效果，得使用`painless`语言来写脚本实现。如示例中的`ctx._source.result=params.result`表示将result这个属性重新赋值为`map`中`"result"`的value，而不会影响其他属性的值。通过`ctx._source.xxx`可访问Document的xxx属性，若需更新多个属性值，赋值语句吉间使用`;`连接，`painless`语言还可实现判断式等复杂脚本，同学们可自行去查阅资料，此处就不再扩展了。
```java
    /**
     * 更新
     */
    @Test
    void testUpdate() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mapDoc = new HashMap<>();
        mapDoc.put("id", "5");
        map.put("result", "我是更新后的5号");
        //使用painless语言和前面的参数创建内嵌脚本
        UpdateQuery updateQuery = UpdateQuery.builder("5").withParams(map).withScript("ctx._source.result=params.result").withLang("painless").withRefresh(UpdateQuery.Refresh.True).build();
        UpdateResponse updateResponse = elasticsearchRestTemplate.update(updateQuery, elasticsearchRestTemplate.getIndexCoordinatesFor(UseLogTwoDO.class));
        System.out.println(updateResponse.getResult());
    }
```
  - 基础"bool"DSL语法实现
  
  `testQuery1()`与`testQuery2()`的查询效果一致，但通过对比，除代码简洁度上不一样，另外`testQuery1()`将查询结果帮我们封装成了目标对象，但`testQuery2()`中的返回结果source只能转换成String或者Map，需要我们手动处理。
  ```java
    /**
     * template查询
     * 查询result一定包含"更新"且sortNo可能大于5的Document
     */
    @Test
    void testQuery1() {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //可继续添加其他条件 should,must,must_not,filter，还可嵌套
        boolQueryBuilder.should(rangeQuery("sortNo").gt(5));
        boolQueryBuilder.must(matchQuery("result", "更新"));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        //设置分页
        nativeSearchQuery.setPageable(PageRequest.of(0,3));
        SearchHits<UseLogTwoDO> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UseLogTwoDO.class);
        List<SearchHit<UseLogTwoDO>> searchHits1 = searchHits.getSearchHits();
        if (!searchHits1.isEmpty()) {
            searchHits1.forEach(hits -> {
                System.out.println(hits.getContent().toString());
                System.out.println("--------------------------------");
            });
        }
    }

    /**
     * client查询
     */
    @Test
    void testQuery2() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.should(rangeQuery("sortNo").gt(5));
        boolQueryBuilder.must(matchQuery("result", "更新"));
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页
        searchSourceBuilder.from(0).size(3);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("demo-use-log2");
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (org.elasticsearch.search.SearchHit hit : response.getHits().getHits()) {
                String sourceAsString = hit.getSourceAsString();
                System.out.println(sourceAsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  ```
  
  - 聚合查询尝试
  
  ES拥有强大的聚合功能，可以通过ES的聚合查询快速地得从大数据中获取数据价值。能否熟练使用ES的聚合功能也算是是否掌握ES的标准之一。所以老铁们抽空可以好好研究下官方文档。
  ```java
      /**
     * 聚合查询，根据createTime字段聚合；可以理解为SQL中的Group By
     */
    @Test
    void testAggregation() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .terms("aggregationResult")
                .field("createTime"));
        SearchHits<UseLogTwoDO> search = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), UseLogTwoDO.class);
        Aggregations aggregations = search.getAggregations();
        ParsedLongTerms aggregationResult = aggregations.get("aggregationResult");
        aggregationResult.getBuckets().forEach(bucket -> {
            System.out.println(bucket.getKey() + ":" +bucket.getDocCount());
        });
    }
  ```
  - 小结
  > 在本节的示例代码中，除了`RestHighLevelClient`和`ElasticsearchRestTemplate`的API使用，不知道小伙伴们有这样的疑问没：既然`ElasticsearchRestTemplate`已经对`RestHighLevelClient`封装了，那我们还需要用到`RestHighLevelClient`不？答案是肯定的，通过示例对比，template在Document结构确定的情况下会很方便，直接创建一个该Document的实体即可；但是在部分场景下Document的结构是不确定，动态的（比如搬移Mysql数据到ES中，每张表的结构是未知的），此时我们就需要使用`RestHighLevelClient`动态生成Mapping及Setting来满足需求。

# 总结-笔者有话说
至此，入门教程就结束了，由于篇幅有限，本教程未能涵盖所有场景，且示例代码均为简单场景。但笔者旨在通过本文让读者能快速上手，体会到对ES操作的乐趣，但这并不意味着读者可以少掉对官方文档阅读的过程。只有在明白原理的情况下，我们才能将其灵活使用。

如果你有什么疑问，欢迎留言。
>源码地址：https://github.com/Xxianglei/Alei/tree/master/SpringBoot%E4%B8%AD%E4%BD%BF%E7%94%A8Elasticsearch%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/spring-data-es-demo1
