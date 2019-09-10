package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

/**
 * A sparse category reference that can be used to uniquely identify a category.
 *
 * @author tyler.vangorder
 */

@Value
@Builder
public class CategoryReference implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Long id;

}
