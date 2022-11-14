package com.learning.bliss.redis;

import com.learning.bliss.redis.jedis.RedisCacheDemo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
    @Resource
    private RedisCacheDemo redisCacheDemo;

    /**
     * Redis keys 命令
     */
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
        System.out.println(stringRedisTemplate.getExpire("zhangsan", TimeUnit.SECONDS));

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

        //GETEX key 该命令用于获取key的值并在 key 存在时删除 key。 目前报错，因为没有对应的 "GETEX" 命令
        /*System.out.println(stringRedisTemplate.opsForValue().getAndExpire("zhangsan", 10, TimeUnit.SECONDS));
        stringRedisTemplate.opsForValue().getAndExpire("zhangsan", Duration.ofSeconds(10));*/

        //PERSIST key 移除 key 的过期时间，key 将持久保持。
        Assertions.assertTrue(stringRedisTemplate.persist("zhangsan"));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        //RANDOMKEY 从当前数据库中随机返回一个 key 。
        System.out.println(stringRedisTemplate.randomKey());

        //RENAME key newkey 修改 key 的名称
        stringRedisTemplate.rename("zhangsan", "lisi");
        Set set1 = new HashSet(1);
        set1.add("lisi");
        Assertions.assertEquals(1, stringRedisTemplate.countExistingKeys(set1).intValue());

        //RENAMENX key newkey 仅当 newkey 不存在时，将 key 改名为 newkey 。
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        Assertions.assertTrue(stringRedisTemplate.renameIfAbsent("lisi", "zhangsan"));

    }

    /**
     * Strings 数据类型相关操作
     */
    @Test
    public void stringRedis(){

        //SET key value 设置指定 key 的值
        stringRedisTemplate.opsForValue().set("刘邦", "汉高祖");

        //GET key 获取指定 key 的值。
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦"));

        //GETSET key value 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
        System.out.println(stringRedisTemplate.opsForValue().getAndSet("刘邦", "泗水亭长"));
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦"));
    }

    @Test
    public void redisCache(){
        RedisCacheDemo.User user = redisCacheDemo.queryUser("444");
        System.out.println(user.toString());
    }

}

