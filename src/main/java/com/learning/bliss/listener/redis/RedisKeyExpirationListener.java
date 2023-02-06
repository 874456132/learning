package com.learning.bliss.listener.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * redis key过期事件监听
 *
 * @Author xuexc
 * @Date 2023/1/30 18:37
 * @Version 1.0
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override

    public void onMessage(Message message, byte[] pattern) {

        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        System.out.println("收到消息" + message);

    }

}
