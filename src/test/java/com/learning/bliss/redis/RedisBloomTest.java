package com.learning.bliss.redis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/30 11:52
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class RedisBloomTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() {
        //初始化一个布隆过滤器
        BloomFilter<String> filter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                1000,//初始容量
                0.01);//误差率

        //key=1001 value=张三
        //将key=1001 放入布隆过滤器
        filter.put("1001");

        stringRedisTemplate.opsForValue().set("1001", "张三");

        if(filter.mightContain("1001")) {
            System.out.println("布隆过滤器存在 1001");
            System.out.println(stringRedisTemplate.opsForValue().get("1001"));
        } else {
            System.out.println("布隆过滤器不存在 1001");
        }

        if(filter.mightContain("1002")) {
            System.out.println("布隆过滤器存在 1002");
        } else {
            System.out.println("布隆过滤器不存在 1002");
        }
    }
}
