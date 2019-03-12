package com.ferguson.cs.product.task.image.integration;

import org.springframework.util.StringUtils;
import com.ferguson.cs.utilities.ArgumentAssert;

/**
 * Abstract implementation of a client providing a basic contract and
 * functionality for implementing integration clients.
 *
 */
public abstract class IntegrationClient implements Client {

	@Override
	public String buildServerPath(String resourcePath) {
		ArgumentAssert.notNullOrEmpty(resourcePath, "resourcePath");
		if (getClientConfiguration() == null) {
			throw new IllegalStateException("Client configuration must be initalized.");
		}
		if (StringUtils.isEmpty(getClientConfiguration().getRemoteServerAddress())) {
			throw new IllegalStateException(
					"Client configuration's remoteServerAddress state can not be null or empty.");
		}
		final String remoteServerAddress = ClientPathHelper
				.appendTrailingSlash(getClientConfiguration().getRemoteServerAddress());
		final String cleanedResourcePath = ClientPathHelper.removeLeadingSlash(resourcePath);
		return remoteServerAddress.concat(cleanedResourcePath);
	}

}
