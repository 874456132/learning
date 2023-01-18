package com.learning.bliss.config.redis;

import com.learning.bliss.annotation.redis.AsyncConsumeStream;
import com.learning.bliss.bean.redis.StreamMessage;
import com.learning.bliss.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * 利用redis原生消息队列实现
 *
 * @Author xuexc
 * @Date 2023/1/6 12:23
 * @Version 1.0
 */
@Slf4j
@Configuration
public class RedisStreamConsumerConfig implements DisposableBean {

    @Resource
    private ApplicationContext context;
    @Resource
    RedisConnectionFactory factory;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ThreadPoolTaskExecutor taskRedisQueueExecutor;

    /**
     * Vector：
     * （1）Vector 继承了AbstractList，实现了List接口。
     * （2）Vector实现了RandmoAccess接口，即提供了随机访问功能。
     * （3）Vector 实现了Cloneable接口，即实现克隆功能。
     * （4）Vector 实现Serializable接口，表示支持序列化。
     * （5）最大的特点是：线程安全的 ，相当于线程安全的 ArrayList 。
     */
    private Vector<StreamMessageListenerContainer<String, ObjectRecord<String, StreamMessage>>> containerList = new Vector<>();

    /**
     * 单批次读取的消息数
     */
    private final int batchSize = 10;

    /**
     * Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
     */
    private final Duration pollTimeout = Duration.ofSeconds(1);


    @Bean(name = "forkJoinPool")
    public ExecutorService forkJoinPool() {
        return new ForkJoinPool();
    }

    @PostConstruct
    public void initRedisStream() throws Exception {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(AsyncConsumeStream.class);
        if (beansWithAnnotation.size() == 0) {
            return;
        }
        for (Object item : beansWithAnnotation.values()) {
            if (!(item instanceof StreamListener)) {
                continue;
            }
            Method method = item.getClass().getDeclaredMethod("onMessage", Record.class);

            AsyncConsumeStream annotation = method.getAnnotation(AsyncConsumeStream.class);
            if (annotation == null) {
                continue;
            }
            creasteSubscription(factory, (StreamListener) item, annotation.streamKey(), annotation.consumerGroup(), annotation.consumerName());
        }
    }

    private Subscription creasteSubscription(RedisConnectionFactory factory, StreamListener streamListener, String streamKey, String group, String consumerName) {
        StreamOperations<String, String, StreamMessage> streamOperations = this.redisTemplate.opsForStream();

        //当消费组或者消费者不存在时，则创建
        if (redisTemplate.hasKey(streamKey)) {
            StreamInfo.XInfoGroups groups = streamOperations.groups(streamKey);
            if (groups.isEmpty()) {
                String groupName = RedisUtil.Streams.createGroup(streamKey, group);
                log.info("creatGroup:{}", groupName);
            } else {
                groups.stream().forEach(g -> {
                    log.info("Redis Stream Groups:{}", g.groupName());
                    StreamInfo.XInfoConsumers consumers = streamOperations.consumers(streamKey, g.groupName());
                    log.info("Redis Stream Consumers:{}", consumers);
                });
            }
        } else {
            String groupName = RedisUtil.Streams.createGroup(streamKey, group);
            log.info("creatGroup:{}", groupName);
        }


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, StreamMessage>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(batchSize)
                        .serializer(new StringRedisSerializer())
                        .executor(taskRedisQueueExecutor)
                        .pollTimeout(pollTimeout)
                        .targetType(StreamMessage.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, StreamMessage>> listenerContainer = StreamMessageListenerContainer.create(factory, options);
        StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());

        Consumer consumer = Consumer.from(group, consumerName);

        Subscription subscription = listenerContainer.receive(consumer, streamOffset, streamListener);
        listenerContainer.start();
        this.containerList.add(listenerContainer);
        return subscription;
    }

    @Override
    public void destroy() {
        this.containerList.forEach(StreamMessageListenerContainer::stop);
    }
}

