package com.api.redis.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.api.redis.entities.User;

@Repository
public class UserDao {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String KEY = "USER_KEY"; // this will help store the objects under correct hashes. insider tip
													// : avoid using the same name as the entity name to avoid classcast
													// exception.

	public User save(User user) {
		redisTemplate.opsForHash().put(KEY, user.getUserId(), user);// operation for hash i.e. store in KV pair.
		return user;
	}

	public User get(String userId) {
		return (User) redisTemplate.opsForHash().get(KEY, userId);
	}

	public Map<Object, Object> findAll() {
		return redisTemplate.opsForHash().entries(KEY);
	}

	public Object delete(String userId) {
		return redisTemplate.opsForHash().delete(KEY, userId);
	}
}
