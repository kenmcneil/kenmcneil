package com.ferguson.cs.product.api.lib.web;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import com.ferguson.cs.utilities.ArgumentAssert;

public class ErrorResponse {

	private final Instant  timestamp;
	private final int statusCode;
	private final String statusReason;
	private final String message;
	private final String path;
	private String serverSideStacktrace;

	/**
	 * This constructor takes the collection of attributes assigned to the web request by Spring framework. See DefaultErrorAttributes for how this map is populated.
	 *
	 * @param attributes
	 */
	public ErrorResponse(Map<String, Object> attributes) {

		ArgumentAssert.notNull(attributes, "error attributes");

		//The key names are based on the DefaultErrorAttributes implementation. If these should change, the error response may not populate properly.
		this.timestamp = Instant.now();
		this.statusCode = getAttribute(attributes, "status")==null?999:getAttribute(attributes, "status");
		this.statusReason = getAttribute(attributes, "error");
		this.message =  getAttribute(attributes, "message");
		this.path = getAttribute(attributes, "path");
		this.serverSideStacktrace =  getAttribute(attributes, "trace");
	}

	public ErrorResponse(HttpStatus httpStatus, String path, Exception exception) {
		ArgumentAssert.notNull(httpStatus, "HTTP Status");
		this.timestamp = Instant.now();
		this.statusCode = httpStatus.value();
		this.statusReason = httpStatus.getReasonPhrase();
		this.message = exception.getLocalizedMessage();
		this.path = path;
		if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
			this.serverSideStacktrace = ExceptionUtils.getStackTrace(exception);
		}
	}

	public Instant getTimestamp() {
		return timestamp;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public String getStatusReason() {
		return statusReason;
	}
	public String getMessage() {
		return message;
	}
	public String getPath() {
		return path;
	}
	public String getServerSideStacktrace() {
		return serverSideStacktrace;
	}

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(Map<String, Object> attributes, String name) {
		return (T) attributes.get(name);
	}

}
