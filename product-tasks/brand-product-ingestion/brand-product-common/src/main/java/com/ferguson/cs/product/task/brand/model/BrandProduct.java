 package com.ferguson.cs.product.task.brand.model;
  
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BrandProduct implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer systemSourceId;
	private String productId;  
	private Date lastModifiedDate;
	private String productName;
	private String title;
	private String description;
	private String shortDescription;
	private String manufacturer;
	private String type;
	private Double retailPrice;
	private String sku;
	private String upc;
	private Boolean isActive;
	private Date dateAvailable;
	private Date dateUpdated;
	private String status;
	private String categoryName;
	private String color;
	private String brandName;
	
	private List<JsonReference> jsonReferences;
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSystemSourceId() {
		return systemSourceId;
	}
	public void setSystemSourceId(Integer systemSourceId) {
		this.systemSourceId = systemSourceId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
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
	public Double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Date getDateAvailable() {
		return dateAvailable;
	}
	public void setDateAvailable(Date dateAvailable) {
		this.dateAvailable = dateAvailable;
	}
	public Date getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public List<JsonReference> getJsonReferences() {
		return jsonReferences;
	}
	public void setJsonReferences(List<JsonReference> jsonReferences) {
		this.jsonReferences = jsonReferences;
	}
	
	
	
	
}
