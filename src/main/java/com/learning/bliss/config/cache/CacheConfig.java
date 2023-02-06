package com.learning.bliss.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.learning.bliss.config.threadPool.ThreadPoolExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/16 20:20
 * @Version 1.0
 */
@Slf4j
@Component
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    private Executor cacheExecutor = ThreadPoolExecutorFactory.threadPoolExecutor5;

    @Bean
    public Caffeine<Object, Object> caffeineCacheConfig() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(7, TimeUnit.DAYS)
                // 初始的缓存空间大小
                .initialCapacity(500)
                // 使用自定义线程池
                .executor(cacheExecutor)
                // 缓存的最大条数
                .maximumSize(1000);
    }

    @Bean
    public RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties) {
        log.info("实例化org.springframework.data.redis.cache.RedisCacheConfiguration对象");
        CacheProperties.Redis cachePropertiesRedis = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        if (cachePropertiesRedis.getTimeToLive() != null) {
            config = config.entryTtl(cachePropertiesRedis.getTimeToLive());
        }
        if (cachePropertiesRedis.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(cachePropertiesRedis.getKeyPrefix());
        }
        if (!cachePropertiesRedis.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!cachePropertiesRedis.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     * @param redisCacheConfiguration
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(RedisCacheConfiguration redisCacheConfiguration) {

        log.info("设置Redis缓存管理器RedisCacheManager对象的序列化方式");

        RedisSerializer<String> strSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);
        // 定制缓存数据序列化方式及时效
        RedisCacheConfiguration config = redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(strSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSeial))
                //不允许缓存值为空
                //.disableCachingNullValues()
                // 过期时间5分钟 默认永不过期
                //.entryTtl(Duration.ofMinutes(5))
                //缓存key前缀
                .prefixCacheNameWith("REDIS:CACHE:");

        return config;
    }

    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     * 复制 {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}逻辑代码
     * 本人觉得这种写法比较冗余
     *
     * @param redisCacheConfiguration
     * @return RedisCacheManager
     */
    @ConditionalOnMissingBean(RedisCacheConfiguration.class)
    RedisCacheConfiguration redisCacheConfig(RedisCacheConfiguration redisCacheConfiguration) {

        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jacksonSeial))
                // 过期时间5分钟 默认永不过期
                //.entryTtl(Duration.ofMinutes(5))
                //缓存key前缀
                .prefixCacheNameWith("REDIS:CACHE:");

        return redisCacheConfiguration;
    }
}
