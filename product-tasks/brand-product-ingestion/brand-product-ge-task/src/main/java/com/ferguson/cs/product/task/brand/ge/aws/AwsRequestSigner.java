package com.ferguson.cs.product.task.brand.ge.aws;

import java.io.IOException;

import org.springframework.http.HttpRequest;

public interface AwsRequestSigner {
	
	/**
	 * Update/Sign headers in provided HTTP request 
	 * using Amazon AWS authorization protocol.
	 * 
	 * @param request - to update/sign
	 * @param body - request body - optional depending on request type
	 * @throws IOException - if authorization fails
	 */
	void signRequest(HttpRequest request, byte[] body) throws IOException;
	
	/**
	 * Lookup whether host name is required in request header.
	 * @return boolean (true = required)
	 */
	boolean getEnforceHost();
	
	/**
	 * Support option to specify whether host name gets
	 * explicitly set in the request header as may be 
	 * required by some APIs (e.g., GE Product API).
	 * 
	 * Note: default is not enforced (false).
	 * 
	 * @param enforceHost - boolean, true sets host in header
	 */
	void setEnforceHost(boolean enforceHost);
}
