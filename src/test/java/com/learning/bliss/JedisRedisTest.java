package com.learning.bliss;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author xuexc
 * @Date 2021/12/8 14:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JedisRedisTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*@Test
    public void testRedis(){
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        System.out.println(stringRedisTemplate.opsForValue().get("zhangsan"));
        Assertions.assertEquals("111", stringRedisTemplate.opsForValue().get("zhangsan"));
    }*/
    @Test
    public void testCache(){
        queryUser("1");
    }
    @Cacheable(cacheManager = "cacheManager", cacheNames = "redisCache", value = "USER:", key="#userId")
    public User queryUser(String userId) {
        User user = new User();
        user.setUserId("1");
        user.setName("张三");
        user.setAge("18");
        System.out.println("查询数据库user=" + user.toString());
        return user;
    }

    @Data
    public class User{
        private String userId;
        private String name;
        private String age;
    }

}

