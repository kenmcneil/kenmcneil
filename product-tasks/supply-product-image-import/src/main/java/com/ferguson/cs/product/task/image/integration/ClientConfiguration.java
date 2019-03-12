package com.ferguson.cs.product.task.image.integration;

import org.springframework.web.client.RestTemplate;

/**
 * Interface defining basic client configuration contract.
 */
public interface ClientConfiguration {
	
	/**
	 * Gets the value for the remote server address servicing client calls for this
	 * configuration.
	 */
	String getRemoteServerAddress();

	/**
	 * Get a RestTemplate instance for making calls on the remote server.
	 */
	RestTemplate getRestTemplate();

}