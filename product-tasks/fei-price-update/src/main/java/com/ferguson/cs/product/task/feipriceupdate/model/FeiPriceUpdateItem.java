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
	private Integer mpid;
	private Integer baseCategoryId;
	private Integer manufacturerId;
	private Integer umrpId;
	private Double mapPrice;
	private Integer feiOwnedProductId;
	private Integer pricebookId;
	private Double preferredVendorCost;
	private Boolean feiOwnedActive;
	// Current PB1 price in pricebook_cost table
	private Double existingPb1Price;
	// New PB1 price in our temp table for the current job run
	private Double newPb1Price;
	private PriceUpdateStatus priceUpdateStatus;
	private ContainerType containerType;
	private FeiPricingType feiPricingType;
	private ProductStatus productStatus;

	// Added for research purposes.
	private String statusMsg;
	private Double margin;
}
