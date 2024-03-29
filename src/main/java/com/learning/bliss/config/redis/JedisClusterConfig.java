package com.learning.bliss.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

/**
 * cluster mode 集群模式配置 通过jedis连接redis配置类
 * 其实不用写此类，本类完全是spring-boot-autocigure自动加载机制的复制，实际只需要配置application.properties 即可
 * <p>
 * spring redis节点配置查看 {@link RedisProperties}
 * SpringBoot自动配置机制查看{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 */
@Profile("cluster")
@EnableConfigurationProperties(RedisProperties.class)
@Component
@Slf4j
public class JedisClusterConfig {


    /**
     * 构建RedisClusterConfiguration对象
     *
     * @param redisProperties
     * @return RedisClusterConfiguration
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(RedisProperties redisProperties) {

        log.info("Redis在cluster模式下实例化org.springframework.data.redis.connection.RedisClusterConfiguration对象");
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
        config.setUsername(redisProperties.getUsername());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        //最大重定向次数
        config.setMaxRedirects(cluster.getMaxRedirects());
        return config;
    }


    /**
     * 构建JedisPoolConfig对象
     *
     * @param redisProperties
     * @return JedisPoolConfig
     */
    @Bean
    public JedisPoolConfig getJedisPoolConfig(RedisProperties redisProperties) {

        log.info("Redis在cluster模式下实例化redis.clients.jedis.JedisPoolConfig对象");
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
     * @param redisClusterConfiguration
     * @param jedisPoolConfig
     * @return JedisConnectionFactory
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration, JedisPoolConfig jedisPoolConfig) {
        log.info("Redis在cluster模式下实例化org.springframework.data.redis.connection.jedis.JedisConnectionFactory对象" + redisClusterConfiguration.getClass().toString());
        // 集群模式
        //默认指定8个核心线程数的队列
        //JedisConnectionFactory factory = new JedisConnectionFactory(redisClusterConfiguration);
        // 使用配置的线程池大小
        return new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
    }
}
