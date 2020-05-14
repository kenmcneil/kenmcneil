package com.ferguson.cs.product.task.feipriceupdate.model;

public enum CostPriceJobStatus implements MessageEnum {
	LOADING(1,"Loading"),
	ENTERED(2,"Entered"),
	PROCESSING(3,"Processing"),
	COMPLETE(4,"Complete"),
	CANCELLED(5,"Cancelled"),
	ERROR_VALIDATION(6,"Error: Validation"),
	ERROR_UPLOAD(7,"Error: Upload");
   
	private final int code;
	private final String messageTemplate;

	private CostPriceJobStatus(int code, String messageTemplate) {
		this.code = code;
		this.messageTemplate = messageTemplate;
	}

	@Override
	public int getCode() { return code; }

	@Override
	public String getMessageTemplate() {
		return messageTemplate;
	}

	@Override
	public String getMessageType() {
		return "UPLOAD";
	}
}
