package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class WiserProductData implements Serializable {

	private static final long serialVersionUID = 4L;

	private Integer sku;
	private String productName;
	private String productUrl;
	private String imageUrl;
	private String brand;
	private String upc;
	private String mpnModelNumber;
	private String l1Category;
	private String l2Category;
	private String productType;
	private Double mapPrice;
	private Boolean onMap;
	private Boolean onPromo;
	private Boolean inStock;
	private Boolean isLtl;
	private Double productPrice;
	private Double cost;
	private String hctCategory;
	private String conversionCategory;
	private String application;
	private Integer saleId;
	private Date dateAdded;
	private Double listPrice;

	public Integer getSku() {
		return sku;
	}

	public void setSku(Integer sku) {
		this.sku = sku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getMpnModelNumber() {
		return mpnModelNumber;
	}

	public void setMpnModelNumber(String mpnModelNumber) {
		this.mpnModelNumber = mpnModelNumber;
	}

	public String getL1Category() {
		return l1Category;
	}

	public void setL1Category(String l1Category) {
		this.l1Category = l1Category;
	}

	public String getL2Category() {
		return l2Category;
	}

	public void setL2Category(String l2Category) {
		this.l2Category = l2Category;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Boolean getOnMap() {
		return onMap;
	}

	public void setOnMap(Boolean onMap) {
		this.onMap = onMap;
	}

	public Boolean getOnPromo() {
		return onPromo;
	}

	public void setOnPromo(Boolean onPromo) {
		this.onPromo = onPromo;
	}

	public Boolean getInStock() {
		return inStock;
	}

	public void setInStock(Boolean inStock) {
		this.inStock = inStock;
	}

	public Boolean getIsLtl() {
		return isLtl;
	}

	public void setIsLtl(Boolean isLtl) {
		this.isLtl = isLtl;
	}

	public Double getMapPrice() {
		return mapPrice;
	}

	public void setMapPrice(Double mapPrice) {
		this.mapPrice = mapPrice;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getHctCategory() {
		return hctCategory;
	}

	public void setHctCategory(String hctCategory) {
		this.hctCategory = hctCategory;
	}

	public String getConversionCategory() {
		return conversionCategory;
	}

	public void setConversionCategory(String conversionCategory) {
		this.conversionCategory = conversionCategory;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Double getListPrice() {
		return listPrice;
	}

	public void setListPrice(Double listPrice) {
		this.listPrice = listPrice;
	}
}
