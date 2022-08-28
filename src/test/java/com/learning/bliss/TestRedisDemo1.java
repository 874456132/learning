package com.learning.bliss;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 使用依赖spring-boot-starter-data-redis自动连接redis
 *
 * @Author xuexc
 * @Date 2021/12/8 14:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedisDemo1 {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testRedis(){
        redisTemplate.opsForValue().set("zhangsan", "111");
        System.out.println(redisTemplate.opsForValue().get("zhangsan"));
        Assertions.assertEquals("111", redisTemplate.opsForValue().get("zhangsan"));
    }
}
