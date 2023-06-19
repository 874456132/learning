package com.learning.bliss.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/16 20:00
 * @Version 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
@Slf4j
public class CacheManager {

    final String [] cacheName = {"CustInfo", "AcctInfo"};

    @ConditionalOnProperty(name = "spring.cache.switch", havingValue = "on")
    @Bean
    public CaffeineCacheManager caffeineCacheManager(Caffeine<Object, Object> caffeineCache) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeineCache);
        // 方法返回值为null，会不会缓存
        caffeineCacheManager.setAllowNullValues(false);

        caffeineCacheManager.setCacheNames(Arrays.asList(cacheName));
        return caffeineCacheManager;
    }

    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     * @param redisCacheConfiguration
     * @param factory
     * @return
     */
    @ConditionalOnProperty(name = "spring.redis.switch", havingValue = "on")
    @Primary
    @Bean
    public RedisCacheManager redisCacheManager(RedisCacheConfiguration redisCacheConfiguration, RedisConnectionFactory factory) {

        log.info("设置Redis缓存管理器RedisCacheManager对象的序列化方式");
        // 分别创建String和JSON格式序列化对象，对缓存数据key和value进行转换
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(factory));
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheWriter, redisCacheConfiguration,
                false, //是否允许在缓存运行中创建新的缓存名称（业务代码中创建）
                cacheName);
        return redisCacheManager;
    }


    /**
     * 设置Redis缓存管理器RedisCacheManager对象的序列化方式
     *
     * 本人觉得这种写法比较冗余
     *
     * @param cacheManagerCustomizers
     * @param redisCacheConfiguration
     * @param redisCacheManagerBuilderCustomizers
     * @param factory
     * @return RedisCacheManager
     */
    @ConditionalOnProperty(name = "spring.redis.switch", havingValue = "on")
    @ConditionalOnMissingBean(RedisCacheManager.class)
    public RedisCacheManager redisCacheManager(CacheManagerCustomizers cacheManagerCustomizers, RedisCacheConfiguration redisCacheConfiguration,
                                   ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                   RedisConnectionFactory factory) {

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory).cacheDefaults(
                redisCacheConfiguration)
                .initialCacheNames(new HashSet<>(Arrays.asList(cacheName)))
                .disableCreateOnMissingCache();
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return cacheManagerCustomizers.customize(builder.build());
    }

    //初始化布隆过滤器，放入到spring容器里面
    @Bean
    public BloomFilter<String> bloomFilter() {
        BloomFilter<String> filter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                1000,//初始容量
                0.01);//误差率
        return filter;
    }

}
