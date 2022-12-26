package com.learning.bliss.redis.queue;

import com.learning.bliss.redis.queue.pubsub.PubClient;
import com.learning.bliss.redis.queue.pubsub.SubClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * redis消息队列-发布订阅Pub/Sub
 *
 * SUBSCRIBE channel [channel] ：订阅一个或多个频道
 * PUBLISH channel msg ：向一个频道发送消息
 * PSUBSCRIBE pattern[pattern] ：订阅与pattern格式匹配的所有频道
 * 优点：
 *      采用发布订阅模型，支持多生产、多消费
 *
 * 缺点：
 *      不支持数据持久化（只能实时获取订阅的频道消息，当客户端离线后，离线后的频道消息不会被保存起来。因此，必须先执行订阅，再等待消息发布）
 *      无法避免消息丢失
 *      消息堆积有上限，超出时数据丢失
 *
 * @Author: xuexc
 * @Date: 2022/12/23 16:34
 * @Version 0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PubSubTest {

    @Test
    public void pubSubJedisTest () throws InterruptedException {
        String clientId = "myclient";
        PubClient pubClient = new PubClient();
        final String channel = "mychannel";
        final SubClient subClient = new SubClient(clientId);
        Thread subThread = new Thread(() -> {
            System.out.println("------------sub----start------------");
            subClient.sub(channel);
            System.out.println("------------sub----end------------");
        });
        subThread.setDaemon(true);
        subThread.start();
        int i = 0;
        while (i < 20) {
            String message = "message--" + i;
            pubClient.pub(channel, message);
            i++;
            Thread.sleep(100);
        }
        subClient.unsubscribe(channel);

    }
}
