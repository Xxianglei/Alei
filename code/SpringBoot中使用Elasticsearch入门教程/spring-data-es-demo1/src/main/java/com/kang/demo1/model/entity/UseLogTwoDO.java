package com.kang.demo1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.Date;

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
