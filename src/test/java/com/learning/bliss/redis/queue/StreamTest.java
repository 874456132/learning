package com.learning.bliss.redis.queue;

import com.learning.bliss.api.redis.ProduceStreamClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * redis消息队列-Stream
 *      消息可回溯
 *      可以多消费者争抢消息，加快消费速度
 *      可以阻塞读取
 *      没有消息漏读的风险
 * Redis 在以下 2 个场景下，都会导致数据丢失：
 *      AOF 持久化配置为每秒写盘，但这个写盘过程是异步的，Redis 宕机时会存在数据丢失的可能
 *      主从复制也是异步的，主从切换时，也存在丢失数据的可能（从库还未同步完成主库发来的数据，就被提成主库）
 *      基于以上原因我们可以看到，Redis 本身的无法保证严格的数据完整性。
 *      像 RabbitMQ 或 Kafka 这类专业的队列中间件，在使用时，一般是部署一个集群，生产者在发布消息时，队列中间件通常会写「多个节点」，以此保证消息的完整性。这样一来，即便其中一个节点挂了，也能保证集群的数据不丢失。
 *      因为 Redis 的数据都存储在内存中，这就意味着一旦发生消息积压，则会导致 Redis 的内存持续增长，如果超过机器内存上限，依旧可能被强行删除。但 Kafka、RabbitMQ 这类消息队列就不一样了，它们的数据都会存储在磁盘上，磁盘的成本要比内存小得多，当消息积压时，无非就是多占用一些磁盘空间，相比于内存，在面对积压时也会更加轻松。
 *  Redis 相比于 Kafka、RabbitMQ，部署和运维更加轻量。如果你的业务场景足够简单，对于数据丢失不敏感，而且消息积压概率比较小的情况下，把 Redis 当作队列是完全可以的。
 *  如果你的业务场景对于数据丢失非常敏感，而且写入量非常大，消息积压时会占用很多的机器资源，那么我建议你使用专业的消息队列中间件。
 * @Author: xuexc
 * @Date: 2022/12/23 16:34
 * @Version 0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class StreamTest {

    @Autowired
    private ProduceStreamClient produceStreamClient;

    @Test
    public void redisStreamTest () {
        produceStreamClient.sendRecord("stream-1", "测试", "bliss","就测试一下");
    }
}
