package com.ferguson.cs.model.product;

import java.io.Serializable;

import com.ferguson.cs.model.asset.ImageResource;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductSeries implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private String tagline;
	private ImageResource imageSplash;

}
