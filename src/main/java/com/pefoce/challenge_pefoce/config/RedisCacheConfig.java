package com.pefoce.challenge_pefoce.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
    ObjectMapper mapper = objectMapper.copy();
    PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
      .allowIfBaseType(Object.class)
      .build();
    mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(mapper);

    return RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofMinutes(10))
      .disableCachingNullValues()
      .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer())) // Chaves serão Strings
      .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer)); // Valores serão JSON com tipo
  }
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisCacheConfiguration cacheConfiguration) {
    return RedisCacheManager.builder(connectionFactory)
      .cacheDefaults(cacheConfiguration)
      .build();
  }
}