package com.example.springboot.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
@Configuration //指示一个类声明一个或多个@Bean方法，并且可以由Spring容器处理，以便在运行时为这些bean生成BeanDefinition和服务请求
@EnableCaching //启用缓存，使用Lettuce,自动注入配置的方式
@AutoConfigureAfter(RedisAutoConfiguration. class) //在加载配置的类之后再加载当前类
public class RedisConfig extends CachingConfigurerSupport {
        /**
         * config RedisTemplate<object, Object>
         */
         @Bean
         @SuppressWarnings("all")
         public RedisTemplate<String, Object> redisTemplate (RedisConnectionFactory factory){
             RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String,Object>();
             redisTemplate.setConnectionFactory(factory);

             // String 序列化方式
             StringRedisSerializer stringSerializer = new StringRedisSerializer();
             // Jackson 序列化方式
             Jackson2JsonRedisSerializer jacksonSerializer = new Jackson2JsonRedisSerializer(Object.class);
             ObjectMapper objectMapper = new ObjectMapper();
             objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
             objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
             jacksonSerializer.setObjectMapper(objectMapper);

             // key采用stringSerializer, value采用jacksonSerializer
             redisTemplate.setKeySerializer(stringSerializer);
             redisTemplate.setHashKeySerializer(stringSerializer);
             redisTemplate.setValueSerializer(jacksonSerializer);
             redisTemplate.setHashValueSerializer(jacksonSerializer);
             return redisTemplate;
         }

    /**
     config CacheManager
     */
    @Bean
    @SuppressWarnings("all")
    public CacheManager cacheManager(LettuceConnectionFactory factory) {
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
        RedisSerializationContext.SerializationPair pair =
                RedisSerializationContext.SerializationPair.fromSerializer(
                          new Jackson2JsonRedisSerializer(Object.class));
        RedisCacheConfiguration config = RedisCacheConfiguration. defaultCacheConfig().serializeValuesWith(pair);
        // default set
        //RedisCacheConfiguration config = RedisCacheConfiguration. defaultCacheConfig();

        return new RedisCacheManager(writer, config);
    }

    //重新定义缓存key的生成策略
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append (method.getName());
            Arrays.asList(params).stream().forEach(item -> {
                sb.append(item.toString());
            });
            return sb.toString();
        };
    }
    }
