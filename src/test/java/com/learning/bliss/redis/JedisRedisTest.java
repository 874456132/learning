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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
        stringRedisTemplate.opsForValue().set("zhang", "111");
        Assertions.assertEquals("111", stringRedisTemplate.opsForValue().get("zhang"));

        //DEL key 该命令用于在 key 存在时删除 key。
        Assertions.assertTrue(stringRedisTemplate.delete("zhang"));

        //GETDEL key 该命令用于获取key的值并在 key 存在时删除 key。 目前报错，因为没有对应的 "GETDEL" 命令
        /*stringRedisTemplate.opsForValue().set("wang", "111");
        System.out.println(stringRedisTemplate.opsForValue().getAndDelete("wang"));*/

        //DUMP key 序列化给定 key ，并返回被序列化的值。
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        System.out.println(stringRedisTemplate.dump("zhangsan"));

        //检查给定 key 是否存在，命令 EXISTS key
        Set set = new HashSet(1);
        set.add("zhangsan");
        Assertions.assertEquals(1, stringRedisTemplate.countExistingKeys(set).intValue());

        //EXPIRE key seconds 为给定 key 设置过期时间
        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", 10, TimeUnit.SECONDS));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", Duration.ofSeconds(100)));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        //EXPIREAT key timestamp EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置过期时间。 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Set<String> keys = stringRedisTemplate.keys("*");
        System.out.println(Arrays.toString(keys.toArray()));
        //keys.stream().forEach(s -> System.out.println(s));

        /*System.out.println(stringRedisTemplate.opsForValue().getAndExpire("zhangsan", 10, TimeUnit.SECONDS));
        opsForValue().getAndExpire("zhangsan", Duration.ofSeconds(10));*/
    }
    @Test
    public void redisCache(){
        RedisCacheDemo.User user = redisCacheDemo.queryUser("444");
        System.out.println(user.toString());
    }

}

