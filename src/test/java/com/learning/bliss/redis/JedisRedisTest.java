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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        Assertions.assertEquals("111", stringRedisTemplate.opsForValue().get("zhangsan"));

        //检查给定 key 是否存在，命令 EXISTS key
        Set set = new HashSet(1);
        set.add("zhangsan");
        Assertions.assertEquals(1, stringRedisTemplate.countExistingKeys(set).intValue());

        //EXPIRE key seconds 为给定 key 设置过期时间
        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", 10, TimeUnit.SECONDS));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", Duration.ofSeconds(100)));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", LocalDate.of(2022, 10, 29).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse("2022-10-30");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", date));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));


        /*System.out.println(stringRedisTemplate.opsForValue().getAndExpire("zhangsan", 10, TimeUnit.SECONDS));
        opsForValue().getAndExpire("zhangsan", Duration.ofSeconds(10));*/
    }
    @Test
    public void redisCache(){
        RedisCacheDemo.User user = redisCacheDemo.queryUser("444");
        System.out.println(user.toString());
    }

}

