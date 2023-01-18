package com.learning.bliss.redis.queue;

import com.learning.bliss.demo.redis.queue.pubsub.PubClient;
import com.learning.bliss.demo.redis.queue.pubsub.SubClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * redis消息队列-发布订阅Pub/Sub
 *
 * subscribe channel [channel] ：订阅一个或多个频道
 * psubscribe pattern[pattern] ：订阅与pattern格式匹配的所有频道
 * publish channel msg ：向一个频道发送消息
 * unsubscribe：取消订阅。
 * 优点：
 *      典型的广播模式，采用发布订阅模型，支持多生产、多消费
 *      多信道订阅，消费者可以同时订阅多个信道，从而接收多类消息。
 *      消息即时发送，消息不用等待消费者读取，消费者会自动接收到信道发布的消息，（publish完成后，监听到订阅频道接受到消息时回调onMessage方法）。
 * 缺点：
 *      不支持数据持久化（只能实时获取订阅的频道消息，当客户端离线后，离线后的频道消息不会被保存起来。因此，必须先执行订阅，再等待消息发布）
 *      无法避免消息丢失（消息堆积有上限，超出时数据丢失。通常发生在消息的生产远大于消费速度时。）
 *      不能保证每个消费者接收的时间是一致的。
 *      可见，Pub/Sub 模式不适合做消息存储，消息积压类的业务，而是擅长处理广播，即时通讯，即时反馈的业务
 * @Author: xuexc
 * @Date: 2022/12/23 16:34
 * @Version 0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
@TestPropertySource(locations="classpath:application.properties")
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
