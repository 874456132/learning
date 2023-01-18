package com.learning.bliss.redis;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

/**
 * 响应式编程
 * ReactiveRedisTemplate 响应式操作类，用来以响应式编程的方式去操作 Redis
 *
 * @Author xuexc
 * @Date 2022/12/30 18:40
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class ReactiveTest {

    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;

    @Test
    public void reactiveValueOperationsRedis() {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ReactiveValueOperations<String, String> ops = reactiveRedisTemplate.opsForValue();
        // 异步设置值
        ops.set("value-key", "value1").subscribe(b -> {
            System.out.println(b ? "设置 value-key 成功" :
                    "设置 value-key 失败");
            countDownLatch.countDown();
        });

        //  异步更新值
        ops.getAndSet("value-key", "value2").subscribe(s -> {
            System.out.println("修改 value-key 失败，旧值：" + s);
            countDownLatch.countDown();
        });

        // 异步批量设置值
        Map<String, String> map = new HashMap<>();
        map.put("value-key1", "value1");
        map.put("value-key2", "value2");
        map.put("value-key3", "value3");
        ops.multiSet(map).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                System.out.println(aBoolean ? "批量设置成功" : "批量设置失败");
                countDownLatch.countDown();
            }
        });

        // 异步追加值到键
        ops.append("value-key", " new value").subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                ops.get("value-key").subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println("新值：" + s);
                        countDownLatch.countDown();
                    }
                });
            }
        });

        // 同步获取数据
        try {
            // 等待设置值完成
            countDownLatch.await();
            Mono<List<String>> mono = ops.multiGet(Arrays.asList("value-key",
                    "value-key1", "value-key3", "value-key2"));
            List<String> list = mono.block();
            for (String value : list) {
                System.out.println(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 异步获取值
        ops.multiGet(Arrays.asList("value-key", "value-key1", "value-key3", "value-key2")).subscribe(s -> {
            System.out.println("value-key=" + s);
            ops.getAndSet("value-key", "new value").subscribe(s1 -> {
                System.out.println("value-key=" + s1);
                countDownLatch.countDown();
            });
        });

        // 异步获取且设置新值
        ops.get("value-key").subscribe(s -> {
            System.out.println("value-key=" + s);
            ops.getAndSet("value-key", "new value").subscribe(s1 -> {
                System.out.println("value-key=" + s1);
                countDownLatch.countDown();
            });
        });
    }


    @SneakyThrows
    @Test
    public void reactiveListOperationsTest() {
        // 利用阻塞队列实现等待
        LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<>();
        ReactiveListOperations<String,String> ops = reactiveRedisTemplate.opsForList();
        // 添加元素
        ops.leftPushAll("list-key", "value1", "value2", "value3", "value4")
                .subscribe(l -> queue.add("leftPushAll 执行成功，添加 " + l + " 个元素"));
        System.out.println(queue.take());//如果队列中没有数据

        // 使用 range 获取列表部分元素
        ops.range("list-key", 0, 2)
                .subscribe(s -> System.out.println(s));
    }

}
