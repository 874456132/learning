package com.learning.bliss.redis.jedis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

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
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RedisProperties.class, CacheProperties.class})
@ConditionalOnProperty(name = "spring.redis.client-type", havingValue = "jedis", matchIfMissing = true)
public class JedisStandaloneConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 构建RedisStandaloneConfiguration对象
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties redisProperties) {
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
     * @return
     */
    @Bean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
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

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnSingleCandidate(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 配置连接工厂
        redisTemplate.setConnectionFactory(factory);
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);


        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 值采用json序列化
        redisTemplate.setValueSerializer(jacksonSeial);

        // 设置hash key 和value序列化模式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jacksonSeial);
        // 设置支持事物
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 实例化 stringRedisTemplate 对象
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnSingleCandidate(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 值采用json序列化
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    /*Redis 缓存管理器*/
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public RedisCacheManager cacheManager(CacheProperties properties, RedisConnectionFactory factory) {
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
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(properties.getRedis().getTimeToLive())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(strSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSeial))
                .disableCachingNullValues();
        return new RedisCacheManager(redisCacheWriter, config);
    }


    /**
     * 复制 {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}逻辑代码
     * 本人觉得这种写法比较冗余
     * @param cacheProperties
     * @param cacheManagerCustomizers
     * @param redisCacheConfiguration
     * @param redisCacheManagerBuilderCustomizers
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    RedisCacheManager cacheManager(CacheProperties cacheProperties, CacheManagerCustomizers cacheManagerCustomizers,
                                   ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration,
                                   ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                   RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
                determineConfiguration(cacheProperties, redisCacheConfiguration));

        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        if (cacheProperties.getRedis().isEnableStatistics()) {
            builder.enableStatistics();
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return cacheManagerCustomizers.customize(builder.build());
    }

    private RedisCacheConfiguration determineConfiguration(
            CacheProperties cacheProperties,
            ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
        return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(cacheProperties));
    }

    private RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties) {
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jacksonSeial));
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