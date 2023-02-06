package com.learning.bliss.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis管理
 * 其实不用写此类，本类完全是spring-boot-autocigure自动加载机制的复制，实际只需要配置application.properties 即可
 * <p>
 * spring redis节点配置查看 {@link RedisProperties}
 * SpringBoot自动配置机制查看{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 */
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
     * 响应式编程操作redis的reactiveRedisTemplate实例
     * @param factory
     * @return
     */
    @Bean
    public ReactiveRedisTemplate<String,String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String,String> builder =
                RedisSerializationContext.newSerializationContext();

        // 设置序列化方式
        builder.key(new StringRedisSerializer());
        builder.value(new StringRedisSerializer());
        builder.hashKey(new StringRedisSerializer());
        builder.hashValue(new StringRedisSerializer());
        builder.string(new StringRedisSerializer());

        RedisSerializationContext build = builder.build();
        ReactiveRedisTemplate<String,String> template = new ReactiveRedisTemplate<String,String>(factory, build);
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(), new PatternTopic("__keyevent@0__:expired"));
        return container;

    }
}