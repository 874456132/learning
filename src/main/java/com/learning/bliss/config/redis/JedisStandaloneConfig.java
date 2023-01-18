package com.learning.bliss.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * standalone mode 单机模式配置  通过jedis连接redis配置类
 * 其实不用写此类，本类完全是spring-boot-autocigure自动加载机制的复制，实际只需要配置application.properties 即可
 * <p>
 * spring redis节点配置查看 {@link RedisProperties}
 * SpringBoot自动配置机制查看{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 *
 * @Author: xuexc
 * @Date: 2022/8/8 23:22
 * @Version 0.1
 */

@Profile("standalone")
@EnableConfigurationProperties({RedisProperties.class, CacheProperties.class})
@Component
@Slf4j
public class JedisStandaloneConfig {

    /**
     * 构建RedisStandaloneConfiguration对象
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties redisProperties) {
        log.info("Redis在standalone模式下实例化org.springframework.data.redis.connection.RedisStandaloneConfiguration对象");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(redisProperties.getPassword());
        config.setUsername(redisProperties.getUsername());
        return config;
    }


    /**
     * 构建jedis连接工厂（对象）
     *
     * @param redisStandaloneConfiguration
     * @return JedisConnectionFactory
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        log.info("Redis在standalone模式下实例化org.springframework.data.redis.connection.jedis.JedisConnectionFactory对象");
        /*从JedisConnectionFactory的构造函数看
        public JedisConnectionFactory(RedisStandaloneConfiguration standaloneConfig) {
            this((RedisStandaloneConfiguration)standaloneConfig, (JedisClientConfiguration)(new JedisConnectionFactory.MutableJedisClientConfiguration()));
        }
        他不是不用pool，而是用了一个自己的JedisConnectionFactory.MutableJedisClientConfiguration()
        而MutableJedisClientConfiguration是一个内类，还不是public的，是protect的。你还没办法在你的configuration里边使用这个类。
        MutableJedisClientConfiguration会自动生成一个JedisPoolConfig，这个JedisPoolConfig继承自GenericObjectPoolConfig，他的pool的配置就是8个。
        所以，如果你要用RedisStandaloneConfiguration，基本上就是8个的pool配置。看来spring 也认为，既然是redis的单机版，8个够用了。*/
        JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration);
        return factory;
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {

        ReactiveRedisConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return factory;
    }

    @Bean
    public RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties) {
        log.info("Redis在standalone模式下实例化org.springframework.data.redis.cache.RedisCacheConfiguration对象");
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}