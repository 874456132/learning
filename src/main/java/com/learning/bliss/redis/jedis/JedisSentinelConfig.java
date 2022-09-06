package com.learning.bliss.redis.jedis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

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
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(name = "spring.redis.client-type", havingValue = "jedis", matchIfMissing = true)
public class JedisSentinelConfig {


    /**
     * 构建RedisClusterConfiguration对象
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(RedisProperties redisProperties) {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
        config.setMaxRedirects(cluster.getMaxRedirects());
        config.setPassword(redisProperties.getPassword());
        return config;
    }


    /**
     * 构建JedisPoolConfig对象
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public JedisPoolConfig getJedisPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接数
        config.setMaxTotal(pool.getMaxActive());
        //最大空闲数
        config.setMaxIdle(pool.getMaxIdle());
        //最小空闲数
        config.setMinIdle(pool.getMinIdle());
        //空闲对象驱逐线程运行之间的时间。当为正时，空闲对象驱逐线程启动，否则不执行空闲对象驱逐
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis());
        }
        //建立连接最大等待时间
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }



    /**
     * 构建jedis连接工厂（对象）
     *
     * @param redisClusterConfiguration
     * @param jedisPoolConfig
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration, JedisPoolConfig jedisPoolConfig) {
        // 集群模式
        JedisConnectionFactory factory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
        return factory;
    }

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 实例化 stringRedisTemplate 对象
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /*Redis 缓存管理器*/
    /*@Bean(name = "redisCache")
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCache = new RedisCacheManager(redisTemplate);
        return redisCache;
    }*/

}
