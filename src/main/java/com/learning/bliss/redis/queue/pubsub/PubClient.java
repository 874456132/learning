package com.learning.bliss.redis.queue.pubsub;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.io.FileReader;
import java.util.Properties;
import java.util.Set;

/**
 * 消息发布
 *
 * @Author: xuexc
 * @Date: 2022/12/23 15:46
 * @Version 0.1
 */
@Slf4j
public class PubClient {


    private Jedis jedis;

    private String CONSTANT_CLIENTSET = "clientSet";

    @SneakyThrows
    public PubClient() {
        Properties properties = new Properties();
        properties.load(new FileReader("E:\\mysoft\\learning-bliss\\src\\main\\resources\\application.properties"));

        String host = properties.getProperty("spring.redis.host");
        String port = properties.getProperty("spring.redis.port");
        String password = properties.getProperty("spring.redis.password");
        jedis = new Jedis(host, Integer.parseInt(port));
        jedis.auth(password);
    }

    private void put(String message) {
        Set<String> subClients = jedis.smembers(CONSTANT_CLIENTSET);
        for (String clientKey : subClients) {
            jedis.rpush(clientKey, message);
        }
    }

    public void pub(String channel, String message) {
        Long txid = jedis.incr("MAXID");
        String content = txid + "/" + message;
        this.put(content);
        jedis.publish(channel, message);
    }

    public void close(String channel) {
        jedis.publish(channel, "quit");
        jedis.del(channel);
    }
}