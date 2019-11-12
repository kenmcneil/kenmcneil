package com.ferguson.cs.product.api.lib;

import java.util.Optional;

import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;

/**
 * This helper class is used to reduce boilerplate code and to provide a consistent way to generate a ResourceNotFoundException.
 *
 * @author tyler.vangorder
 */
public final class OptionalResourceHelper {

	private OptionalResourceHelper() {
	}

	/** This method will either return the value (if present in the optional) or it will throw a "resource not found" exception.
	 *
	 * @param resource The optional resource
	 * @param resourceType The resource type is a human readable type description that will be used in the "resource not found" message.
	 * @param resourceId The unique ID used to retrieve the resource
	 * @return The value, if present.
	 * @throws ResourceNotFoundException If the resource is not present.
	 */
	public static <T> T handle(Optional<T> resource, String resourceType, Object resourceId) {
		return resource.orElseThrow(() -> new ResourceNotFoundException(
				String.format("The %s [%s] was not found.", resourceType, resourceId)));
	}
}
