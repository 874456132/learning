package com.learning.bliss.listener.redis;

import com.learning.bliss.annotation.redis.AsyncConsumeStream;
import com.learning.bliss.bean.redis.StreamMessage;
import com.learning.bliss.constant.RedisConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通过监听器异步消费
 *
 * @Author xuexc
 * @Date 2023/1/4 23:18
 * @Version 1.0
 */
@Slf4j
@Getter
@Setter
public class ConsumeStreamListener implements StreamListener<String, ObjectRecord<String, StreamMessage>> {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    @AsyncConsumeStream(streamKey = RedisConstant.STREAM_KEY, consumerGroup = RedisConstant.STREAM_GROUP, consumerName = RedisConstant.CONSUMER_NAME)
    public void onMessage(ObjectRecord<String, StreamMessage> record) {
        System.err.println("redis stream onMessage start:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        String stream = record.getStream();
        RecordId id = record.getId();
        StreamMessage value = record.getValue();

        //业务逻辑

        //当是消费组消费时，如果不是自动ack，则需要在这个地方手动ack
        redisTemplate.opsForStream().acknowledge(RedisConstant.STREAM_KEY, RedisConstant.STREAM_GROUP,id);
    }
}
