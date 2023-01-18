package com.learning.bliss.config.redis;

import com.learning.bliss.annotation.redis.AsyncConsumeLists;
import com.learning.bliss.api.redis.ListsListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 利用redis Lists数据类型的 push和pop实现消息队列
 *
 * @Author xuexc
 * @Date 2023/1/6 12:22
 * @Version 1.0
 */
@Slf4j
@Component
public class RedisListsConsumerConfig implements DisposableBean {

    @Resource
    private ApplicationContext context;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ThreadPoolTaskExecutor taskRedisQueueExecutor;

    private boolean isClose = false;

    @SneakyThrows
    @PostConstruct
    public void init() {
        Map<String, ListsListener> beanMap = context.getBeansOfType(ListsListener.class);
        if (beanMap.size() == 0) {
            return;
        }
        for (ListsListener listsListener : beanMap.values()) {
            Method method = listsListener.getClass().getDeclaredMethod("onMessage", Object.class);
            AsyncConsumeLists asyncConsumeLists = method.getAnnotation(AsyncConsumeLists.class);
            if (asyncConsumeLists == null) {
                continue;
            }
            String queue = asyncConsumeLists.queue();
            log.info("消息队列名称:{}", queue);

            taskRedisQueueExecutor.execute(() -> {
                long timeout = asyncConsumeLists.timeout();

                while (!isClose) {
                    try {
                        Object message = stringRedisTemplate.opsForList().rightPop(queue, timeout, TimeUnit.SECONDS);
                        if (message == null) {
                            continue;
                        }
                        listsListener.onMessage(message);
                    } catch (Exception e) {
                        log.error("队列{}读取消息失败:", queue, e);
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e1) {
                            log.error("sleep失败:", e1);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        isClose = true;
        taskRedisQueueExecutor.shutdown();
    }
}
