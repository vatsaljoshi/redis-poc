package com.api.redis.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This controller simulates a fraud detection API endpoint. In a real system,
 * you would send transaction/user data to a fraud detection service, which
 * would analyze it and respond if the activity is suspicious.
 * 
 * Here, we randomly decide if a request is "fraud" or "not fraud" for
 * demonstration/testing purposes.
 */
@RestController
@RequestMapping("/mock/fraud-detection")
public class FraudDetectionController {

	// Used to generate random results for the mock
	private final Random random = new Random();

	/**
	 * POST endpoint that "detects fraud".
	 * 
	 * @param payload Any JSON sent by the client (ignored in this simple mock).
	 * @return A JSON map indicating if the request is considered fraud and a
	 *         confidence score.
	 */
	@PostMapping
	public Map<String, Object> detectFraud(@RequestBody Map<String, Object> payload) {
		// Simulate fraud: 20% chance of being flagged as fraud
		boolean isFraud = random.nextInt(100) < 20; // n could be any integer from 0 to 99

		// Prepare the mock response
		Map<String, Object> result = new HashMap<>();
		result.put("fraud", isFraud); // true if fraud, false otherwise
		result.put("confidence", random.nextDouble()); // This line puts a random confidence score between 0.0 and 1.0
														// in the response.

		return result;
	}
}
