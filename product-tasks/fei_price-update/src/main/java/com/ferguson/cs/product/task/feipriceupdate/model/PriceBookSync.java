package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PriceBookSync implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer jobId;
	private String productId;
	private String manufacturer;
	private String finish;
	private Integer uniqueId;
	private Double listPrice;
	private Integer priceBookId;
	private Double cost;
	private Boolean isDelete;
	private Boolean hasError;
	private String syncErrorReason;
	private ProductSyncErrorType syncErrorType;

	
}
