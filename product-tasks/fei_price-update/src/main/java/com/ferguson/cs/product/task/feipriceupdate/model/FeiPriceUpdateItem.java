package com.ferguson.cs.product.task.feipriceupdate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeiPriceUpdateItem {	
	private String tempTableName = "temp_fei_price_update";
	private Integer uniqueId;
	private Double price;
	private String priceRule;
	private Integer mpid;
	private Integer baseCategoryId;
	private Integer manufacturerId;
}
