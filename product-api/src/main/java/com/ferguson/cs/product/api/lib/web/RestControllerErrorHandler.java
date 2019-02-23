package com.ferguson.cs.product.api.lib.web;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;

@RestControllerAdvice
@RestController
public class RestControllerErrorHandler extends ResponseEntityExceptionHandler implements ErrorController {

    private static final String ERROR_PATH = "/error";

    private final ErrorAttributes errorAttributes;

	public RestControllerErrorHandler(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	@GetMapping(value = ERROR_PATH)
	public ErrorResponse error(WebRequest request) {
		return new ErrorResponse(errorAttributes.getErrorAttributes(request, false));
	}

	@ExceptionHandler({ResourceNotFoundException.class})
	public final ResponseEntity<Object> handleResourceNotException(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler({Exception.class})
	public final ResponseEntity<Object> handleDefaultException(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST);
		}
		String path = null;
		if (request instanceof ServletWebRequest) {
			path = ((ServletWebRequest)request).getRequest().getRequestURI();
		}

		return new ResponseEntity<>(new ErrorResponse(status, path, exception), headers, status);
	}

}
