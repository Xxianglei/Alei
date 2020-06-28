package com.kang.demo1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

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

    @Id
    private String id;

    private Integer sortNo;

    @Field(type = FieldType.Keyword)
    private String result;

    private Date createTime;
}
