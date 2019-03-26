package com.ferguson.cs.product.task.image.integration.webservices;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ferguson.cs.product.task.image.SupplyProductImageFileNameHelper;
import com.ferguson.cs.product.task.image.integration.ClientConfiguration;
import com.ferguson.cs.product.task.image.integration.IntegrationClient;
import com.ferguson.cs.utilities.ArgumentAssert;



/**
 * A client for the build-api app to request resources and functionality from
 * the webservices api.
 */
@Service
public class WebservicesClient extends IntegrationClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebservicesClient.class);

	@Autowired
	private WebservicesClientConfiguration webservicesClientConfiguration;

	@Override
	public ClientConfiguration getClientConfiguration() {
		return webservicesClientConfiguration;
	}

	@Override
	public RestTemplate getRestTemplate() {
		return webservicesClientConfiguration.getRestTemplate();
	}

	@PostConstruct
	public void reportConfiguration() {
		LOGGER.info("webservices-client configuration:");
		if (StringUtils.isEmpty(webservicesClientConfiguration.getRemoteServerAddress())) {
			throw new IllegalStateException(
					"Configuration not set for webservices-client, missing remote-server-address.");
		}
		LOGGER.info(
				String.format("- remote-server-address: %s", webservicesClientConfiguration.getRemoteServerAddress()));
		if (getRestTemplate() == null) {
			throw new IllegalStateException("Configuration not set for webservices-client, missing rest-template.");
		}
	}

	/**
	 * Gets a boolean indicating the ability to communicate with the webservices
	 * service. Returns true if communication is known good and false otherwise.
	 * 
	 * @return
	 */
	public boolean serverUp() {
		try {
			getRestTemplate().getForObject(this.buildServerPath("/app/health"), Object.class);
		} catch (RestClientException e) {
			LOGGER.warn("Webservices server can not be communicated with.", e);
			return false;
		}
		return true;
	}

	/**
	 * Uploads a product image file for the supply-brand. Adds or replaces the image
	 * file and associates the file using contracted file naming techniques.
	 * 
	 * see webservices for implementation details ...
	 * /v1/business-units/{business-unit}/upload-product-image
	 * 
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
	public ProductImageUploadResult uploadSupplyProductImageIOStream(String fileName, ImageFileResource inputStream) {
		ArgumentAssert.notNullOrEmpty(fileName, "fileName");
		ArgumentAssert.notNull(inputStream, "inputStream");
		final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("file", inputStream);
		parts.add("file-name", fileName);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts);
		final ResponseEntity<ProductImageUploadResult> result = getRestTemplate().exchange(
				this.buildServerPath("/v1/business-units/{business-unit}/upload-product-image"), HttpMethod.POST,
				requestEntity, ProductImageUploadResult.class,
				SupplyProductImageFileNameHelper.BUSINESS_UNIT_ID_SUPPLY);
		if (result != null) {
			LOGGER.info("Cloudinary Uploaded FileName "+result.getBody().getUploadFileName());
			LOGGER.info("Cloudinary PublicId is "+result.getBody().getPublicId());
			LOGGER.info("Cloudinary URI is "+result.getBody().getUriList());
		
		}	
		return result.getBody();
	}

}
