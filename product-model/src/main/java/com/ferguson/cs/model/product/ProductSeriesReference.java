package com.ferguson.cs.model.product;

import java.io.Serializable;

import org.springframework.data.repository.query.Param;

import com.ferguson.cs.model.asset.ImageResource;

import lombok.Value;

/**
 * A product series reference.
 *
 * @author tyler.vangorder
 */
@Value
public class ProductSeriesReference implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Integer id;
	private final String name;
	private final String description;
	private final String tagline;
	private final ImageResource imageSplash;

	public ProductSeriesReference(
			@Param("id") Integer id,
			@Param("name") String name,
			@Param("description") String description,
			@Param("tagline") String tagline,
			@Param("imageSplash") ImageResource imageSplash) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.tagline = tagline;
		this.imageSplash = imageSplash;
	}

	public ProductSeriesReference(ProductSeries productSeries) {
		this.id = productSeries.getId();
		this.name = productSeries.getName();
		this.description = productSeries.getDescription();
		this.tagline = productSeries.getTagline();
		this.imageSplash= productSeries.getImageSplash();
	}
}
