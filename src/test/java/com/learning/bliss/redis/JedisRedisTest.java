package com.learning.bliss.redis;

import com.learning.bliss.redis.jedis.RedisCacheDemo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author xuexc
 * @Date 2021/12/8 14:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class JedisRedisTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisCacheDemo redisCacheDemo;

    @Test
    public void keysRedis(){
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        System.out.println(stringRedisTemplate.opsForValue().get("zhangsan"));
        Assertions.assertEquals("111", stringRedisTemplate.opsForValue().get("zhangsan"));
    }
    @Test
    public void redisCache(){
        RedisCacheDemo.User user = redisCacheDemo.queryUser("444");
        System.out.println(user.toString());
    }

}

