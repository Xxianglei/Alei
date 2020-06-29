package com.kang.demo1;

import com.kang.demo1.dao.UseLogRepository;
import com.kang.demo1.model.entity.UseLogDO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <h3>spring-data-elasticsearch-demo</h3>
 * <p></p>
 *
 * @author yingKang
 * @date 2020-06-05 17:19
 */
@SpringBootTest
public class EsFirstDemoTest {

    @Resource
    private UseLogRepository useLogRepository;

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

    /**
     * 删
     */
    @Test
    void testDelete() {
        long deleteNumber = useLogRepository.deleteBySortNoIsGreaterThan(0);
        System.out.println("删除日志数量为：" + deleteNumber);
    }

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
}
