package com.learning.bliss.api.redis;

import com.learning.bliss.bean.redis.StreamMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis stream 消息生产客户端
 *
 * @Author xuexc
 * @Date 2023/1/4 23:42
 * @Version 1.0
 */
@Component
@Slf4j
public class ProduceStreamClient {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void sendRecord(String key, String title, String author, Object content) {
        StreamMessage msg = StreamMessage.create(title, author, content);
        log.info("生产消息 >>> " + msg);

        //RecordId 为自动生成
        ObjectRecord<String, StreamMessage> record = StreamRecords.objectBacked(msg).withStreamKey(key);

        RecordId recordId = redisTemplate.opsForStream().add(record);

        log.info("返回的record-id:[{}]", recordId);
    }
}
