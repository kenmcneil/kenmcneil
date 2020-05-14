package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeiPriceUpdateItem implements Serializable {	

	private static final long serialVersionUID = 1L;
	
	private String tempTableName;
	private Integer uniqueId;
	private Double price;
	private String priceRule;
	private Integer mpid;
	private Integer baseCategoryId;
	private Integer manufacturerId;
	private Integer umrpId;
	private Integer feiOwnedProductId;
	private Integer pricebookId;
}
