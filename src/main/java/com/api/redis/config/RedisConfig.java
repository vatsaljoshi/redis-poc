package com.api.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * necessary pre-configuration to redis implementation.
 * 
 * @author vatsaljoshi
 */

@Configuration
public class RedisConfig {

	@Bean
	public RedisConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(connectionFactory()); // connection factory
		redisTemplate.setKeySerializer(new StringRedisSerializer()); // key serializer
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // value serializer
		return redisTemplate;
	}

}
