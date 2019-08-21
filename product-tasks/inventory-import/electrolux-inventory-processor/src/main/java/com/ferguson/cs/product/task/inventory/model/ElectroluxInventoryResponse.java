package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="InventoryCheckResponse",namespace = "http://eluxna.com")
@XmlAccessorType(XmlAccessType.NONE)
public class ElectroluxInventoryResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "AvailableQuantity", namespace = "http://eluxna.com")
	private Double availableQuantity;

	@XmlElement(name = "Error")
	private String error;

	@XmlElement(name = "ErrorDescription")
	private String errorDescription;

	public Double getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Double availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
