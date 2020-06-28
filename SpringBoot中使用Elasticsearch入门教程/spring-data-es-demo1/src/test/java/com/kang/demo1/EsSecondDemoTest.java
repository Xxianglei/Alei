package com.kang.demo1;

import com.kang.demo1.model.entity.UseLogTwoDO;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p>ES进阶测试</p>
 *
 * @author yingKang
 * @date 2020-06-27 15:45
 */
@SpringBootTest
public class EsSecondDemoTest {

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

    /**
     * 批量添加
     */
    @Test
    void testAdd() {
        List<IndexQuery> list = new ArrayList<>(100);
        for (int i = 1; i <= 100; i++) {
            IndexQuery indexQuery = new IndexQuery();
            UseLogTwoDO useLogDO = UseLogTwoDO.builder().id(String.valueOf(i)).sortNo(i).result(String.format("我是%d类", i%5)).createTime(new Date()).build();
            indexQuery.setId(useLogDO.getId());
            indexQuery.setObject(useLogDO);
            list.add(indexQuery);
        }
        List<String> strings = elasticsearchRestTemplate.bulkIndex(list, elasticsearchRestTemplate.getIndexCoordinatesFor(UseLogTwoDO.class));
        System.out.println(strings);
    }

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

}
