package com.ferguson.cs.product.test;

import java.util.UUID;

import org.springframework.util.Assert;

public class GeneralTestUtilities {

	public static String randomString() {
		return UUID.randomUUID().toString();
	}
	public static String randomString(int size) {
		Assert.isTrue(size > 0, "The size cannot be negative.");
		Assert.isTrue(size < 37, "The size cannot exceed 36 characters, which is the size of the returned UUID as a string.");
		return randomString().substring(0, size);
	}

}
