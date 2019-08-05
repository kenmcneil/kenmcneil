package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;

public class ProductData implements Serializable {
	private static final long serialVersionUID = 2L;

	private String productId;
	private Integer compositeId;
	private Integer uniqueId;
	private String productTitle;
	private String finish;
	private String image;
	private String description;
	private String upc;
	private String manufacturer;
	private String type;
	private String mpn;
	private String baseCategory;
	private String businessCategoryName;
	private Double mapPrice;
	private Boolean isMap;
	private Boolean inStock;
	private Boolean isPromo;
	private Double price;
	private Double cost;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(Integer compositeId) {
		this.compositeId = compositeId;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMpn() {
		return mpn;
	}

	public void setMpn(String mpn) {
		this.mpn = mpn;
	}

	public String getBaseCategory() {
		return baseCategory;
	}

	public void setBaseCategory(String baseCategory) {
		this.baseCategory = baseCategory;
	}

	public String getBusinessCategoryName() {
		return businessCategoryName;
	}

	public void setBusinessCategoryName(String businessCategoryName) {
		this.businessCategoryName = businessCategoryName;
	}

	public Boolean getMap() {
		return isMap;
	}

	public void setMap(Boolean map) {
		isMap = map;
	}

	public Boolean getInStock() {
		return inStock;
	}

	public void setInStock(Boolean nonStock) {
		inStock = nonStock;
	}

	public Boolean getPromo() {
		return isPromo;
	}

	public void setPromo(Boolean promo) {
		isPromo = promo;
	}

	public Double getMapPrice() {
		return mapPrice;
	}

	public void setMapPrice(Double mapPrice) {
		this.mapPrice = mapPrice;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}
}
