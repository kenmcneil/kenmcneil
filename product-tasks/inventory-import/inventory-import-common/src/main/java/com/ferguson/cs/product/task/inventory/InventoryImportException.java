package com.ferguson.cs.product.task.inventory;

public class InventoryImportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InventoryImportException(Throwable ex) {
		super(ex);
	}

	public InventoryImportException(String message, Throwable ex) {
		super(message,ex);
	}

	public InventoryImportException(String message) {
		super(message);
	}
}
