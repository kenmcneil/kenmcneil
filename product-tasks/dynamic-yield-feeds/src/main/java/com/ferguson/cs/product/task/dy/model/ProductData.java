package com.ferguson.cs.product.task.dy.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductData implements Serializable {
	private static final long serialVersionUID = 3L;

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

	public String getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(String siteIds) {
		this.siteIds = siteIds;
	}

	public Boolean getCaliforniaDroughtCompliant() {
		return californiaDroughtCompliant;
	}

	public void setCaliforniaDroughtCompliantDroughtCompliant(Boolean californiaDroughtCompliant) {
		this.californiaDroughtCompliant = californiaDroughtCompliant;
	}

	public String getBaseCategory() {
		return baseCategory;
	}

	public void setBaseCategory(String baseCategory) {
		this.baseCategory = baseCategory;
	}

	public String getBusinessCategory() {
		return businessCategory;
	}

	public void setBusinessCategory(String businessCategory) {
		this.businessCategory = businessCategory;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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
