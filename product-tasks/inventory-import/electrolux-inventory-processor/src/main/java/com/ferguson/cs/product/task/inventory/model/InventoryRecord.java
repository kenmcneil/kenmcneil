package com.ferguson.cs.product.task.inventory.model;

public class InventoryRecord {

	private Double inTransitQuantity;
	private String modelNumber;
	private Double netInventory;
	private String nextAvailabilityDate;
	private String warehouseCode;


	public Double getInTransitQuantity() {
		return inTransitQuantity;
	}

	public void setInTransitQuantity(Double inTransitQuantity) {
		this.inTransitQuantity = inTransitQuantity;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public Double getNetInventory() {
		return netInventory;
	}

	public void setNetInventory(Double netInventory) {
		this.netInventory = netInventory;
	}

	public String getNextAvailabilityDate() {
		return nextAvailabilityDate;
	}

	public void setNextAvailabilityDate(String nextAvailabilityDate) {
		this.nextAvailabilityDate = nextAvailabilityDate;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
}
