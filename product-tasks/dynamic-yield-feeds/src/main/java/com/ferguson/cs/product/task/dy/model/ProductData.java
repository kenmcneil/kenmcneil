package com.ferguson.cs.product.task.dy.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductData implements Serializable {
	private static final long serialVersionUID = 5L;

	private Integer sku;
	private Integer groupId;
	private String name;
	private String status;
	private BigDecimal price;
	private String image;
	private String model;
	private String manufacturer;
	private String series;
	private String theme;
	private String genre;
	private String finish;
	private BigDecimal rating;
	private String type;
	private String application;
	private String handletype;
	private String masterfinish;
	private String mountingType;
	private String installationType;
	private Integer numberOfBasins;
	private BigDecimal nominalLength;
	private BigDecimal nominalWidth;
	private Integer numberOfLights;
	private String chandelierType;
	private String pendantType;
	private String fanType;
	private String fuelType;
	private String configuration;
	private Boolean californiaDroughtCompliant;
	private String baseCategory;
	private String businessCategory;
	private String siteIds;
	private String encodedCategories;
	private Boolean isConfigurableProduct;
	private Integer count;
}
