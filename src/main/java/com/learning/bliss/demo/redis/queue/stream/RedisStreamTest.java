package com.learning.bliss.demo.redis.queue.stream;

import com.learning.bliss.client.redis.ProduceStreamClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/10 22:39
 * @Version 1.0
 */
public class RedisStreamTest {
    @Autowired
    private static ProduceStreamClient produceStreamClient;

    public static void main(String[] args) {
        produceStreamClient.sendRecord("stream-1", "测试", "bliss","就测试一下");
    }
}
