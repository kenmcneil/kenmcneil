package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceBookLoadCriteria implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer jobId;
	private String tempTableName;
	private Boolean deleteCost;
}
