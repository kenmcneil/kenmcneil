package com.ferguson.cs.product.task.brand.model;

import java.io.Serializable;
import java.util.Map;

public class ProductJson implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer jsonId;
	private Integer productId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getJsonId() {
		return jsonId;
	}
	public void setJsonId(Integer jsonId) {
		this.jsonId = jsonId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	
	
    
}
