package com.ferguson.cs.product.task.dy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class DynamicYieldProduct implements Serializable {
	private static final long serialVersionUID = 5L;

	private Integer sku;
	private Integer groupId;
	private String name;
	private String url;
	private BigDecimal price;
	private Boolean inStock;
	private String imageUrl;
	private String categories;
	private String model;
	private String manufacturer;
	private Boolean discontinued;
	private String series;
	private String theme;
	private String genre;
	private String finish;
	private BigDecimal rating;
	private Boolean hasImage;
	private String relativePath;
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
	private String baseCategory;
	private String businessCategory;
	private Boolean californiaDroughtCompliant;
	private List<Integer> siteIds;
	private Map<Integer, Set<String>> categoryNameSiteMap;
	private Map<Integer, List<Integer>> categoryIdSiteMap;
	private String keywords;
	private String categoryIds;
	private Boolean hasPricedOptions;
	private Boolean hasRecommendedOptions;
}
