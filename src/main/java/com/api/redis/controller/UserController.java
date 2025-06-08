package com.api.redis.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.redis.dao.UserDao;
import com.api.redis.entities.User;
import com.api.redis.ratelimiter.TokenBucketRateLimiter;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TokenBucketRateLimiter rateLimiter;
	
	// helper to get key for rate limit (we are using user's IP here, can use userId, etc.)
	private String getRateLimitKey(HttpServletRequest request) {
		return "rate:users:" + request.getRemoteAddr();
	}

	// create user
	@PostMapping
	public Object createUser(@RequestBody User user, HttpServletRequest request) {
		if(!rateLimiter.allowRequest(getRateLimitKey(request))) {
            return "Rate limit exceeded. Please try again later.";
		}
		user.setUserId(UUID.randomUUID().toString());
		return userDao.save(user);
	}

	// get user
	@GetMapping("/{userId}")
	public Object getUser(@PathVariable("userId") String userId, HttpServletRequest request) {
		if(!rateLimiter.allowRequest(getRateLimitKey(request))) {
            return "Rate limit exceeded. Please try again later.";
		}
		return userDao.get(userId);
	}

	// find all user
	@GetMapping
	public Object getAll(HttpServletRequest request) {
		if(!rateLimiter.allowRequest(getRateLimitKey(request))) {
            return "Rate limit exceeded. Please try again later.";
		}
		return userDao.findAll();
	}

	// delete user
	@DeleteMapping("/{userId}")
	public Object deleteUser(@PathVariable("userId") String userId, HttpServletRequest request) {
		if(!rateLimiter.allowRequest(getRateLimitKey(request))) {
            return "Rate limit exceeded. Please try again later.";
		}
		return userDao.delete(userId);
	}
}
