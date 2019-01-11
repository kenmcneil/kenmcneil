package com.ferguson.cs.model.image;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This is a persistent representation of a resource image, I am not sure yet what the structure of this model will look like, just using it as marker for now.
 * @author tyler.vangorder
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ImageResource implements Serializable {

	private static final long serialVersionUID = 1L;


}
