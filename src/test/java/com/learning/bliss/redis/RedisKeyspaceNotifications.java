package com.learning.bliss.redis;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * redis事件通知
 *
 * @Author xuexc
 * @Date 2023/1/30 17:31
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class RedisKeyspaceNotifications {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SneakyThrows
    @Test
    public void test() {

       /* new Thread(() -> {
            stringRedisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    redisConnection.subscribe((msg, pattern) -> {
                        System.out.println("收到消息" + msg);
                    }, "__keyevent@0__:".getBytes());
                    return null;
                }
            });
        }).start();*/
        //监听在com.learning.bliss.config.redis.RedisManager.container
        stringRedisTemplate.opsForValue().set("0000", "sasada",1, TimeUnit.SECONDS);
        Thread.sleep(2000);

    }
}
