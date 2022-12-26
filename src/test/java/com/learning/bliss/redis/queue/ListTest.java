package com.learning.bliss.redis.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于List结构模拟消息队列
 * Lists是一个双向列表，使用LPUSH、RPOP；或者RPUSH、LPOP来实现，不过要注意的是，当队列中没有消息时，
 * LPOP、RPOP会返回null，并不像JVM的阻塞队列那样阻塞并等待消息，因此需要使用BRPOP或者BLPOP来实现阻塞效果
 * 优点：
 *     1、用redis存储，不受限于JVM内存限制
 *     2、基于redis的持久化机制，数据安全性有保证
 *     3、基于Lists实现，满足消息有序性
 * 缺点：
 *     1、无法避免消息丢失
 *     2、只支持单消费者
 * @Author: xuexc
 * @Date: 2022/12/23 22:41
 * @Version 0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class ListTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void listsQueueRedis() throws InterruptedException {
        /**
         * 基于List结构模拟消息队列
         * Lists是一个双向列表，使用LPUSH、RPOP；或者RPUSH、LPOP来实现，不过要注意的是，当队列中没有消息时，
         * LPOP、RPOP会返回null，并不像JVM的阻塞队列那样阻塞并等待消息，因此需要使用BRPOP或者BLPOP来实现阻塞效果
         * 优点：
         *     1、用redis存储，不受限于JVM内存限制
         *     2、基于redis的持久化机制，数据安全性有保证
         *     3、基于Lists实现，满足消息有序性
         * 缺点：
         *     1、无法避免消息丢失
         *     2、只支持单消费者
         */
        String lQueueKey = "list-queue-car";
        //生产者
        new Thread(() -> {
            List<String> list = new ArrayList<>();
            list.add("唐");
            list.add("元");
            list.add("宋");
            redisTemplate.opsForList().leftPushAll(lQueueKey, list);
            System.out.println("生产者生产：" + Arrays.toString(list.toArray()));
        }).start();
        //消费者
        for (; ; ) {
            Thread.sleep(100);
            new Thread(() -> {
                System.out.println("消费者消费：" + redisTemplate.opsForList().rightPop(lQueueKey, 0, TimeUnit.SECONDS));
            }).start();
        }
    }
}
