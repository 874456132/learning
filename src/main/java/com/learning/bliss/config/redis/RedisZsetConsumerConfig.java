package com.learning.bliss.config.redis;

import com.learning.bliss.annotation.redis.AsyncConsumeZset;
import com.learning.bliss.api.redis.ZsetListener;
import com.learning.bliss.config.threadPool.ThreadPoolExecutorFactory;
import com.learning.bliss.utils.RedisLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 利用Zset实现消息队列
 *
 * @Author xuexc
 * @Date 2023/1/6 12:24
 * @Version 1.0
 */
@Slf4j
@Component
public class RedisZsetConsumerConfig implements InitializingBean {

    @Resource
    private RedisLock redisLock;

    @Resource
    private ApplicationContext context;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private AtomicBoolean isClose = new AtomicBoolean(false);

    private ExecutorService executorService = ThreadPoolExecutorFactory.threadPoolExecutor3;

    @SneakyThrows
    @Override
    public void afterPropertiesSet() {
        Map<String, ZsetListener> beanMap = context.getBeansOfType(ZsetListener.class);
        if (beanMap.size() == 0) {
            return;
        }
        for (ZsetListener consumer : beanMap.values()) {
            Method method = consumer.getClass().getDeclaredMethod("onMessage", Object.class);
            AsyncConsumeZset annotation = method.getAnnotation(AsyncConsumeZset.class);
            if (annotation == null) {
                continue;
            }
            String queue = annotation.queue();
            log.info("延迟消息队列名称:{}", queue);

            execute(queue, consumer);
        }
    }
    @Async("taskRedisQueueExecutor")
    void execute(String queue, ZsetListener consumer) {
        while (!isClose.get()) {
            try {
                Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(queue, 0, 0);

                if (typedTuples == null || typedTuples.isEmpty()) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                }

                Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
                while (iterator.hasNext()) {
                    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();

                    Object value = typedTuple.getValue();
                    Double score = typedTuple.getScore();

                    long now = System.currentTimeMillis();
                    if (now >= score) {
                        try {
                            if (redisLock.lock(queue, score.toString())) {
                                consumer.onMessage(value);

                                //消费完成后将该值移出zset
                                redisTemplate.opsForZSet().remove(queue, value);
                            }
                        } finally {
                            redisLock.unlock(queue, score.toString());
                        }
                    }
                }

            } catch (Exception e) {
                log.error("延迟队列{}读取消息失败:{}", queue, e);
            }
        }
    }


    @PreDestroy
    public void preDestory() {
        isClose.set(true);
        executorService.shutdown();
    }

}