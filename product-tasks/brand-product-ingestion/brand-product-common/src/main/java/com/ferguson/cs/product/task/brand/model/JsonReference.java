package com.ferguson.cs.product.task.brand.model;

import java.io.Serializable;

public class JsonReference implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private JsonType jsonType;
	private String jsonString;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public JsonType getJsonType() {
		return jsonType;
	}
	public void setJsonType(JsonType jsonType) {
		this.jsonType = jsonType;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
    
}
