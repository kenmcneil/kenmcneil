package com.ferguson.cs.model.asset;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
/**
 * This is model for representing an image resource: jpg, gif, etc.
 */
public class ImageResource implements DigitalResource {

	private static final long serialVersionUID = 1L;

	private final String resourcePath;

	@Override
	public DigitalResourceType getType() {
		return DigitalResourceType.IMAGE;
	}

}
