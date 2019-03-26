package com.ferguson.cs.product.task.image.integration;

import org.springframework.web.client.RestTemplate;

/**
 * Interface defining basic client contract.  
 */
public interface Client {
	
	/**
	 * Gets the client's configuration.
	 * @return
	 */
	ClientConfiguration getClientConfiguration();

	/**
	 * Gets the RestTemplate used by the client.
	 * @return
	 */
	RestTemplate getRestTemplate();
	
	/**
	 * Builds the full server path to the the passed resource path.
	 * 
	 * @param resourcePath
	 * @return
	 */
	String buildServerPath(String resourcePath);

}
