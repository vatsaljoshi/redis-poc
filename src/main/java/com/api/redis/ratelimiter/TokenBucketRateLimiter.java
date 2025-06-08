package com.api.redis.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenBucketRateLimiter {
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${ratelimiter.capacity:10}")
	private int capacity;

	@Value("${ratelimiter.refillRate:5}")
	private int refillRate;

	public boolean allowRequest(String key) {
		// 1. Define the Redis keys for storing the number of tokens and the last refill
		// time
		String tokensKey = key + ":tokens";
		String lastRefillKey = key + ":lastRefill";

		// 2. Get the current time in milliseconds
		long now = System.currentTimeMillis();

		// 3. Lua script to atomically update and check the token bucket in Redis
		String luaScript = "local tokens = tonumber(redis.call('get', KEYS[1]) or ARGV[1]) "
				+ "local lastRefill = tonumber(redis.call('get', KEYS[2]) or ARGV[2]) "
				+ "local capacity = tonumber(ARGV[3]) " + "local refillRate = tonumber(ARGV[4]) "
				+ "local now = tonumber(ARGV[5]) "
				// Calculate the elapsed time since the last refill
				+ "local delta = math.max(0, now - lastRefill) "
				// Figure out how many tokens to add based on elapsed time and refill rate
				+ "local refill = math.floor(delta / 1000 * refillRate) "
				// Update tokens, but don't exceed capacity
				+ "tokens = math.min(capacity, tokens + refill) "
				// If tokens are available, allow the request and decrease by one
				+ "if tokens > 0 then " + "  tokens = tokens - 1; " + "  redis.call('set', KEYS[1], tokens) "
				+ "  redis.call('set', KEYS[2], now) " + "  return 1 "
				// If no tokens, deny the request
				+ "else " + "  redis.call('set', KEYS[1], tokens) " + "  redis.call('set', KEYS[2], now) "
				+ "  return 0 " + "end";

		// 4. Execute the Lua script in Redis, passing in the keys and arguments
		Long allowed = redisTemplate
				.execute((RedisCallback<Long>) connection -> (Long) connection.eval(luaScript.getBytes(), // The script
																											// (as
																											// bytes)
						ReturnType.INTEGER, // We want an integer result (1 or 0)
						2, // Number of keys we're passing (tokensKey, lastRefillKey)
						tokensKey.getBytes(), // Key 1: tokens
						lastRefillKey.getBytes(), // Key 2: last refill timestamp
						String.valueOf(capacity).getBytes(), // ARGV[1]: initial tokens if not exist
						String.valueOf(now).getBytes(), // ARGV[2]: initial lastRefill if not exist
						String.valueOf(capacity).getBytes(), // ARGV[3]: capacity
						String.valueOf(refillRate).getBytes(), // ARGV[4]: refill rate
						String.valueOf(now).getBytes() // ARGV[5]: current time
				));

		// 5. If the script returns 1, we allow the request, otherwise we deny it
		return allowed != null && allowed == 1;
	}
}