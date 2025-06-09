package com.api.redis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * This controller simulates an API that sometimes "sheds load" (rejects
 * requests). Load shedding is typically used to protect a system under heavy
 * load by temporarily denying some requests so the system doesn't get
 * overwhelmed.
 * 
 * Here, requests are randomly rejected with an HTTP 503 error for
 * demonstration/testing.
 */
@RestController
@RequestMapping("/mock/load-shedding")
public class LoadSheddingController {

	// Used to randomly decide whether to shed load
	private final Random random = new Random();

	/**
	 * GET endpoint that sometimes rejects requests to simulate load shedding.
	 * 
	 * @return 503 Service Unavailable some of the time, 200 OK otherwise.
	 */
	@GetMapping
	public ResponseEntity<String> maybeShedLoad() {
		// Simulate load shedding: 30% chance the request is rejected
		if (random.nextInt(100) < 30) {
			// Return HTTP 503 with a message
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body("Service is temporarily overloaded. Please try again.");
		}
		// Otherwise, allow the request
		return ResponseEntity.ok("Request succeeded!");
	}
}
