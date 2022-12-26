package com.learning.bliss.redis.queue.pubsub;

import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.FileReader;
import java.util.Properties;

/**
 * 消息订阅
 *
 * @Author: xuexc
 * @Date: 2022/12/23 16:22
 * @Version 0.1
 */
public class SubClient extends JedisPubSub {


    private Jedis jedis;

    private JedisPubSub listener;
    private String CONSTANT_CLIENTSET = "clientSet";

    @SneakyThrows
    public SubClient(String clientId) {
        Properties properties = new Properties();
        properties.load(new FileReader("E:\\mysoft\\learning-bliss\\src\\main\\resources\\application.properties"));

        String host = properties.getProperty("spring.redis.host");
        String port = properties.getProperty("spring.redis.port");
        String password = properties.getProperty("spring.redis.password");
        jedis = new Jedis(host, Integer.parseInt(port));
        jedis.auth(password);
        listener = new PubSubListener(clientId, jedis);
        jedis.sadd(CONSTANT_CLIENTSET, clientId);
    }

    /**
     * @param channel
     */
    public void sub(String channel) {
        jedis.subscribe(listener, channel);
    }

    /**
     * 取消订阅模式时的回调( onPUnsubscribe )
     *
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("onPSubscribe---pattern:" + pattern + ",subscribedChannels:" + subscribedChannels);
    }

    /**
     * 取消订阅频道时的回调（onUnsubscribe）
     *
     * @param channel
     */
    public void unsubscribe(String channel) {
        listener.unsubscribe(channel);
    }
}