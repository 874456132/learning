package com.learning.bliss.redis.jedis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * sentinel mode 哨兵模式配置  通过jedis连接redis配置类
 * 其实不用写此类，本类完全是spring-boot-autocigure自动加载机制的复制，实际只需要配置application.properties 即可
 * <p>
 * spring redis节点配置查看 {@link RedisProperties}
 * SpringBoot自动配置机制查看{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 *
 * @Author: xuexc
 * @Date: 2022/8/8 23:22
 * @Version 0.1
 */
@Profile("sentinel")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RedisProperties.class, CacheProperties.class})
@Slf4j
public class JedisSentinelConfig {


    /**
     * RedisSentinelConfiguration
     * copy by {@link org.springframework.boot.autoconfigure.data.redis.RedisConnectionConfiguration#getSentinelConfig()}
     * @param redisProperties
     * @return RedisSentinelConfiguration
     */
    @Bean
    protected RedisSentinelConfiguration redisSentinelConfiguration(RedisProperties redisProperties) {
        log.info("Redis在sentinel模式下实例化org.springframework.data.redis.connection.RedisSentinelConfiguration对象");
        RedisProperties.Sentinel sentinelProperties = redisProperties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            List<RedisNode> nodes = new ArrayList<>();
            for (String node : sentinelProperties.getNodes()) {
                try {
                    String[] parts = StringUtils.split(node, ":");
                    Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                    nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
                }
                catch (RuntimeException ex) {
                    throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
                }
            }
            config.setSentinels(nodes);
            config.setUsername(redisProperties.getUsername());
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                config.setPassword(RedisPassword.of(redisProperties.getPassword()));
            }
            config.setSentinelUsername(sentinelProperties.getUsername());
            if (StringUtils.isNotBlank(sentinelProperties.getPassword())) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            config.setDatabase(redisProperties.getDatabase());
            return config;
        }
        return null;
    }


    /**
     * 构建JedisPoolConfig对象
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public JedisPoolConfig getJedisPoolConfig(RedisProperties redisProperties) {
        log.info("Redis在sentinel模式下实例化redis.clients.jedis.JedisPoolConfig对象");
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接数
        config.setMaxTotal(pool.getMaxActive());
        //最大空闲数
        config.setMaxIdle(pool.getMaxIdle());
        //最小空闲数
        config.setMinIdle(pool.getMinIdle());
        return config;
    }



    /**
     * 构建jedis连接工厂（对象）
     *
     * @param redisSentinelConfiguration
     * @param jedisPoolConfig
     * @return JedisConnectionFactory
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisSentinelConfiguration redisSentinelConfiguration, JedisPoolConfig jedisPoolConfig) {
        log.info("Redis在sentinel模式下实例化org.springframework.data.redis.connection.jedis.JedisConnectionFactory对象");
        // 集群模式
        return new JedisConnectionFactory(redisSentinelConfiguration, jedisPoolConfig);
    }

}
