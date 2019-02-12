package com.ferguson.cs.product.task.brand.ge.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;



public class AwsClientHttpRequestFactory extends InterceptingClientHttpRequestFactory {
	private static final Logger LOG = LoggerFactory.getLogger(AwsClientHttpRequestFactory.class);

	public AwsClientHttpRequestFactory(AwsRequestSigner requestSigner) {
		this(defaultRequestFactory(), requestSigner);
	}

	public AwsClientHttpRequestFactory(ClientHttpRequestFactory requestFactory, AwsRequestSigner requestSigner) {
		super(requestFactory, Collections.singletonList(
				LOG.isDebugEnabled()
					? new LoggingAwsClientHttpRequestInterceptor(requestSigner)
					: new AwsClientHttpRequestInterceptor(requestSigner)));
	}

	private static ClientHttpRequestFactory defaultRequestFactory() {
		SimpleClientHttpRequestFactory simpleRequestFactory = new SimpleClientHttpRequestFactory();
		// We need to disable this because it does not allow for reading 401 responses.
		simpleRequestFactory.setOutputStreaming(false);
		return simpleRequestFactory;
	}

	private static class AwsClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
		private final AwsRequestSigner requestSigner;

		protected AwsClientHttpRequestInterceptor(AwsRequestSigner requestSigner) {
			this.requestSigner = requestSigner;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			requestSigner.signRequest(request, body);
			return execution.execute(request, body);
		}
	}

	
	private static final class LoggingAwsClientHttpRequestInterceptor extends AwsClientHttpRequestInterceptor {
		public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
		
		private LoggingAwsClientHttpRequestInterceptor(AwsRequestSigner requestSigner) {
			super(requestSigner);
		}
		public static Charset getCharset(HttpHeaders headers) {
			if (headers == null) {
				return DEFAULT_CHARSET;
			}
			MediaType mediaType = headers.getContentType();
			if (mediaType == null) {
				return DEFAULT_CHARSET;
			}
			Charset charset = mediaType.getCharset();
			return charset == null ? DEFAULT_CHARSET : charset;
		}


		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			String requestBodyMessage = "";
			if (body != null && body.length > 0) {
				String requestBodyString;
				try {
					requestBodyString =  getCharset(request.getHeaders()).newDecoder().decode(ByteBuffer.wrap(body)).toString();
				} catch(IllegalStateException | CharacterCodingException ex) {
					LOG.warn("Unable to read request body", ex);
					requestBodyString = "[Unable to read request body]";
				}

				requestBodyMessage = ", with request body: " + requestBodyString;
				if (requestBodyMessage.contains("\n")) {
					requestBodyMessage += "\n";
				} else {
					requestBodyMessage += ", ";
				}
			}

			LOG.debug("Performing AWS...");
			ClientHttpResponse response = new BufferedClientHttpResponse(super.intercept(request, body, execution));

			String messageStart = request.getMethod()
					+ " request for \"" + request.getURI() + "\" " + requestBodyMessage + "resulted in "
					+ response.getRawStatusCode() + " (" + response.getStatusCode().getReasonPhrase() + ")";
			// Try to read the body
			try {
				try(Reader reader = new InputStreamReader(response.getBody(), getCharset(response.getHeaders()))) {
					String responseBody = IOUtils.toString(reader);
					LOG.debug(messageStart + " with body: " + responseBody);
				}
			} catch(Exception ex){
				LOG.debug(messageStart + " with error reading body", ex);
			}
			return response;
		}
	}

	private static final class BufferedClientHttpResponse implements ClientHttpResponse {

		private final ClientHttpResponse response;
		private byte[] body;

		private BufferedClientHttpResponse(ClientHttpResponse response) {
			this.response = response;
		}


		@Override
		public HttpStatus getStatusCode() throws IOException {
			return response.getStatusCode();
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return response.getRawStatusCode();
		}

		@Override
		public String getStatusText() throws IOException {
			return response.getStatusText();
		}

		@Override
		public void close() {
			response.close();
		}

		@Override
		public InputStream getBody() throws IOException {
			if (body == null) {
				body = IOUtils.toByteArray(response.getBody());
			}
			return new ByteArrayInputStream(body);
		}

		@Override
		public HttpHeaders getHeaders() {
			return response.getHeaders();
		}
	}
}
