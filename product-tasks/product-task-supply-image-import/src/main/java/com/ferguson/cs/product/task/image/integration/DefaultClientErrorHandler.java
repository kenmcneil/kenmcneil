package com.ferguson.cs.product.task.image.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.ferguson.cs.utilities.JsonUtils;

/**
 * Defines a default client error handler which can be used in implementations
 * of a ClientConfiguration to set an errorHandler for a given client
 * implementation.
 * 
 * You may want to define your own handler in which case it is acceptable and
 * recommended to extend this class and override handleError.
 * 
 * One could implement a Spring DefaultResponseErrorHandler just as easy.
 * However if we ever want common behavior or state we would have a good
 * delivery point here when the DefaultClientErrorHandler is extended.
 * 
 */
public class DefaultClientErrorHandler extends DefaultResponseErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientErrorHandler.class);

	/**
	 * Handles any error response from server calls performed by clients.
	 * Throws a RestClientException including any details from the server error response.
	 * 
	 * Clients can catch/handle (RestClientException) if they have a strategy.  Otherwise the exception will be handled by our common Rest controller handlers (v1 or v2 and greater).
	 * v1 uses the base ServiceController handlers.
	 * v2 uses the ResponseBodyAdvice handlers.
	 * 
	 * v1 behavior change (minimal and should not be breaking).
	 *  - Bug: Was Looking for singular RestError instances which would have blown and thrown a HttpClientErrorException or HttpServerErrorException or RestClientException (Spring) in past.
	 *  - Overall we will prevent non RestClientException types from getting processed by more generic handlers in any common handlers.
	 *  
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {

		if (response == null) {
			throw new IllegalArgumentException("Passed response can not be null.");
		}

		List<ResponseError> responseErrors = new ArrayList<>();
		if (response.getBody() != null) {
			byte[] bodyBytes = null;
			try {
				bodyBytes = FileCopyUtils.copyToByteArray(response.getBody());
			} catch (Exception e) {
				LOGGER.error("Failed to convert response body to byte array, response will not be deserialized. Error while handling error response from server.", e);
			}
			if (bodyBytes != null) {
				try {	
					Collections.addAll(responseErrors, JsonUtils.readValue(new ByteArrayInputStream(bodyBytes), ResponseError[].class));
				} catch (Exception e1) {
					// not a List, see if we have singular RestError
					ResponseError responseError = null;
					try {
						responseError = JsonUtils.readValue(new ByteArrayInputStream(bodyBytes), ResponseError.class);
						responseErrors.add(responseError);
					} catch (Exception e2) {
						// allow RestClientException to throw.  Log occurrence ..
						LOGGER.error("Failed to deserialize RestError/RestError[].  Error while handling error response from server.", e2);
					}
				}
			}
		}

		throw new RestClientException(responseErrors.get(0).getMessage());

	}

}
