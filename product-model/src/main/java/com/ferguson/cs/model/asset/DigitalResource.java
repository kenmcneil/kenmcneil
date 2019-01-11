package com.ferguson.cs.model.asset;

import java.io.Serializable;

/**
 * This is an interface for representing a digital resource, these can include images, pdfs, 3d models, etc.
 *
 * TODO: I am not sure yet what the structure of this model will look like, just using it as marker for now.
 *
 * @author tyler.vangorder
 */
public interface DigitalResource extends Serializable {

	/**
	 * The type of digital resource.
	 *
	 * @return type
	 */
	DigitalResourceType getType();
}
