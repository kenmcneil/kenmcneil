package com.ferguson.cs.product.task.inventory.model;

import java.util.ArrayList;
import java.util.List;

public class EmailInventoryImportJobLog extends InventoryImportJobLog {

	private static final long serialVersionUID = 1L;

	private List<InventoryImportJobEmailAttachment> inventoryImportJobEmailAttachmentList = new ArrayList<>();

	public List<InventoryImportJobEmailAttachment> getInventoryImportJobEmailAttachmentList() {
		return inventoryImportJobEmailAttachmentList;
	}
}
