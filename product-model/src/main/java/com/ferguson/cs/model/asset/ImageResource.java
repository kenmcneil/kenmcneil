package com.ferguson.cs.model.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
/**
 * This is model for representing an image resource: jpg, gif, etc.
 */
public class ImageResource implements DigitalResource {

	private static final long serialVersionUID = 1L;

	@Override
	public DigitalResourceType getType() {
		return DigitalResourceType.IMAGE;
	}

}
