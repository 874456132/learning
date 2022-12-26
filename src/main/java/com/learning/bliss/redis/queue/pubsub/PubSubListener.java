package com.learning.bliss.redis.queue.pubsub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;

/**
  * Listener类用于处理消息
  * @Author: xuexc
  * @Date: 2022/12/23 19:03
  * @Version 0.1
  */
public class PubSubListener extends JedisPubSub {


    private String clientId;
    private SubHandler handler;
    private String CONSTANT = "clientSet";
    public PubSubListener(String clientId, Jedis jedis) {
        this.clientId = clientId;
        handler = new SubHandler(jedis);
    }

    private void message(String channel, String message) {
        Date time = new Date();
        System.out.println("message receive:" + message + ",channel:" + channel + time.toString());
    }

    /**
     * 监听到订阅频道接受到消息时的回调 (onMessage)
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(String channel, String message) {
        if (message.equalsIgnoreCase("quit")) {
            this.unsubscribe(channel);
        }
        handler.handle(channel, message);
        System.out.println("message receive:" + message + ",channel:" + channel);
    }

    @Override
    public void unsubscribe(String... channels) {
        super.unsubscribe(channels);
        for (String channel : channels) {
            handler.unsubscribe(channel);
        }
    }

    /**
     * 监听到订阅模式接受到消息时的回调 (onPMessage)
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println("Pattern:" + pattern + ",Channel:" + channel + ",Message:" + message);
    }

    /**
     * 订阅频道时的回调（onSubscribe）
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("onSubscribe---channel:" + channel + ",subscribedChannels:" + subscribedChannels);
    }

    /**
     * 取消订阅频道时的回调（onUnsubscribe）
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("onUnsubscribe---channel:" + channel + ",subscribedChannels:" + subscribedChannels);
    }

    /**
     * 订阅频道模式时的回调 (onPSubscribe)
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        System.out.println("onPUnsubscribe---pattern:" + pattern + ",subscribedChannels:" + subscribedChannels);
    }

    class SubHandler {
        private Jedis jedis;

        SubHandler(Jedis jedis) {
            this.jedis = jedis;
        }

        public void handle(String channel, String message) {
            int index = message.indexOf("/");
            if (index < 0) {
                return;
            }
            Long txid = Long.valueOf(message.substring(0, index));
            String key = clientId + "/" + channel;
            while (true) {
                String lm = jedis.lindex(key, 0);
                if (lm == null) {
                    break;
                }
                int li = lm.indexOf("/");
                if(li<0){
                    String result = jedis.lpop(key);
                    if(result == null){
                        break;
                    }
                    message(channel, lm);
                    continue;
                }
                Long lxid = Long.valueOf(lm.substring(0, li));
                if(txid>=lxid){
                    jedis.lpop(key);
                    message(channel,lm);
                    continue;
                }else{
                    break;
                }
            }
        }
        public void subscribe(String channel){
            String key = clientId+"/"+channel;
            boolean exist = jedis.sismember(CONSTANT, key);
            if(!exist){
                jedis.sadd(CONSTANT, key);
            }
        }
        public void unsubscribe(String channel){
            String key = clientId+"/"+channel;
            jedis.srem(CONSTANT, key);
            jedis.del(key);
        }
    }

}
