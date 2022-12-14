package com.learning.bliss.redis.jedis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

/**
 * 通过jedis连接redis配置类
 * 其实不用写此类，本类完全是spring-boot-autocigure自动加载机制的复制，实际只需要配置application.properties 即可
 * <p>
 * spring redis节点配置查看 {@link RedisProperties}
 * SpringBoot自动配置机制查看{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 */
@ConditionalOnProperty(name = "spring.redis.client-type", havingValue = "jedis", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
@Slf4j
public class RedisManager {

    /**
     * 设置RedisTemplate对象的序列化方式
     * @param factory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {

        log.info("设置RedisTemplate对象的序列化方式");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 配置连接工厂
        redisTemplate.setConnectionFactory(factory);
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        RedisSerializer<String> serializerStringKey = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializerStringKey);
        // 值采用json序列化
        redisTemplate.setValueSerializer(jacksonSeial);

        // 设置hash key 和value序列化模式
        RedisSerializer<String> serializerHashKey = new StringRedisSerializer();
        redisTemplate.setHashKeySerializer(serializerHashKey);
        redisTemplate.setHashValueSerializer(jacksonSeial);
        // 设置支持事物
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 设置stringRedisTemplate对象的序列化方式
     * @param factory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {

        log.info("设置stringRedisTemplate对象的序列化方式");
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        RedisSerializer<String> serializerStringKey = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(serializerStringKey);
        // 值采用json序列化
        RedisSerializer<String> serializerStringValue = new StringRedisSerializer();
        stringRedisTemplate.setValueSerializer(serializerStringValue);
        return stringRedisTemplate;
    }

    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     * @param redisCacheConfiguration
     * @param factory
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration, RedisConnectionFactory factory) {

        log.info("设置Redis缓存管理器RedisCacheManager对象的序列化方式");
        // 分别创建String和JSON格式序列化对象，对缓存数据key和value进行转换
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(factory));

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
                .disableCachingNullValues();
        return new RedisCacheManager(redisCacheWriter, config);
    }


    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     * 复制 {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}逻辑代码
     * 本人觉得这种写法比较冗余
     *
     * @param cacheManagerCustomizers
     * @param redisCacheConfiguration
     * @param redisCacheManagerBuilderCustomizers
     * @param factory
     * @return RedisCacheManager
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    RedisCacheManager cacheManager(CacheManagerCustomizers cacheManagerCustomizers, RedisCacheConfiguration redisCacheConfiguration,
                                   ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                   RedisConnectionFactory factory) {

        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jacksonSeial));
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory).cacheDefaults(
                redisCacheConfiguration);

        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return cacheManagerCustomizers.customize(builder.build());
    }
}