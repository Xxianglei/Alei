package com.kang.demo1.dao;

import com.kang.demo1.model.entity.UseLogDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p></p>
 *
 * @author yingKang
 * @date 2020-06-05 17:56
 */
public interface UseLogRepository extends ElasticsearchRepository<UseLogDO, String> {

    /**
     * 查找时间段内日志
     * @param startTime
     * @param endTime
     * @return
     */
    List<UseLogDO> findByCreateTimeBetween(long startTime, long endTime);

    /**
     * 分页查询
     * @param start
     * @param end
     * @param page
     * @return
     */
    Page<UseLogDO> findBySortNoIsBetween(int start, int end, Pageable page);
    /**
     * 删除大于deleteStartNo的日志
     * @param deleteStartNo
     * @return
     */
    Long deleteBySortNoIsGreaterThan(int deleteStartNo);

}
