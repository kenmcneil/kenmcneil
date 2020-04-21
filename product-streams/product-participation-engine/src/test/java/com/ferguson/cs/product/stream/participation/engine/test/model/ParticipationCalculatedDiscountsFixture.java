package com.ferguson.cs.product.stream.participation.engine.test.model;

public class ParticipationCalculatedDiscountsFixture {
	private Integer pricebookId;
	private Double changeValue;
	private Boolean isPercent;
	private Integer templateId;

	public Integer getPricebookId() {
		return pricebookId;
	}

	public void setPricebookId(Integer pricebookId) {
		this.pricebookId = pricebookId;
	}

	public Double getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(Double changeValue) {
		this.changeValue = changeValue;
	}

	public Boolean getIsPercent() {
		return isPercent;
	}

	public void setIsPercent(Boolean isPercent) {
		this.isPercent = isPercent;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}
}
