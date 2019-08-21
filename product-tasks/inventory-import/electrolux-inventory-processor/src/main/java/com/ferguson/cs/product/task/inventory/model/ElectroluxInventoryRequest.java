package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InventoryCheckRequest",namespace = "http://eluxna.com")
@XmlAccessorType(XmlAccessType.NONE)
public class ElectroluxInventoryRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "CustomerID",namespace = "http://eluxna.com")
	private String customerId;
	@XmlElement(name = "ItemID",namespace = "http://eluxna.com")
	private String itemId;
	@XmlElement(name = "WareHouseCode",namespace = "http://eluxna.com")
	private String wareHouseCode;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getWareHouseCode() {
		return wareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}
}
