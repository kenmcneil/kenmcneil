package com.ferguson.cs.product.task.image.integration;

import java.io.Serializable;

public class ResponseError implements Serializable {
	private static final long serialVersionUID = 3L;

	private String code;
	private String message;
	private String error;

	public ResponseError() {
	}

	public ResponseError(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResponseError(Throwable cause) {
		setErrorFromThrowable(cause);
		if (cause != null) {
			this.message = cause.toString();
		}
	}

	public ResponseError(String code, String message, Throwable cause) {
		this.code = code;
		this.message = message;
		setErrorFromThrowable(cause);
	}

	public ResponseError(String code, Throwable cause) {
		this.code = code;
		if (cause != null) {
			this.message = cause.toString();
		}
		setErrorFromThrowable(cause);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	private void setErrorFromThrowable(Throwable e) {
		if (e != null) {
			this.error = e.getClass().getName();
		}
	}

}
