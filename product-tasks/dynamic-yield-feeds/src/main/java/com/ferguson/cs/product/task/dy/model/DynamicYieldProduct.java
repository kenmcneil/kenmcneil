package com.ferguson.cs.product.task.dy.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DynamicYieldProduct implements Serializable {
	private static final long serialVersionUID = 1L;

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
	private Boolean CADroughtCompliant;

	public Boolean getCADroughtCompliant() {
		return CADroughtCompliant;
	}

	public void setCADroughtCompliant(Boolean CADroughtCompliant) {
		this.CADroughtCompliant = CADroughtCompliant;
	}

	public Integer getSku() {
		return sku;
	}

	public void setSku(Integer sku) {
		this.sku = sku;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Boolean getInStock() {
		return inStock;
	}

	public void setInStock(Boolean inStock) {
		this.inStock = inStock;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Boolean getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(Boolean discontinued) {
		this.discontinued = discontinued;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public Boolean getHasImage() {
		return hasImage;
	}

	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getHandletype() {
		return handletype;
	}

	public void setHandletype(String handletype) {
		this.handletype = handletype;
	}

	public String getMasterfinish() {
		return masterfinish;
	}

	public void setMasterfinish(String masterfinish) {
		this.masterfinish = masterfinish;
	}

	public String getMountingType() {
		return mountingType;
	}

	public void setMountingType(String mountingType) {
		this.mountingType = mountingType;
	}

	public String getInstallationType() {
		return installationType;
	}

	public void setInstallationType(String installationType) {
		this.installationType = installationType;
	}

	public Integer getNumberOfBasins() {
		return numberOfBasins;
	}

	public void setNumberOfBasins(Integer numberOfBasins) {
		this.numberOfBasins = numberOfBasins;
	}

	public BigDecimal getNominalLength() {
		return nominalLength;
	}

	public void setNominalLength(BigDecimal nominalLength) {
		this.nominalLength = nominalLength;
	}

	public BigDecimal getNominalWidth() {
		return nominalWidth;
	}

	public void setNominalWidth(BigDecimal nominalWidth) {
		this.nominalWidth = nominalWidth;
	}

	public Integer getNumberOfLights() {
		return numberOfLights;
	}

	public void setNumberOfLights(Integer numberOfLights) {
		this.numberOfLights = numberOfLights;
	}

	public String getChandelierType() {
		return chandelierType;
	}

	public void setChandelierType(String chandelierType) {
		this.chandelierType = chandelierType;
	}

	public String getPendantType() {
		return pendantType;
	}

	public void setPendantType(String pendantType) {
		this.pendantType = pendantType;
	}

	public String getFanType() {
		return fanType;
	}

	public void setFanType(String fanType) {
		this.fanType = fanType;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
}
