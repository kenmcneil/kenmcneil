package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ferguson.cs.model.image.ImageResource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Variant implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String productId;
	private String description;
	private VariantStatus status;
	private BigDecimal msrp;
	//TODO Not sure if it makes sense to have priceDiscount here, this might be a channel-specific value.
	//private BigDecimal priceDiscount;
	
	private List<VariantIdentifier> identifierList;
	private List<VariantAttribute> attributeList;

	private BigDecimal weight;
	private BigDecimal freightCost;
	private BigDecimal handlingFee;
	private BigDecimal handlingFeeByItem;
	private BigDecimal dropShipFee;		

	private Boolean isFreeShipping = false;
	private Boolean isFreight = false;
	private Boolean isShippable = true;
	private Boolean isShippableToForeignCountry = false;
	

	private List<ImageResource> imageList;


	//TODO Need to figure out what to do with auditing columns (timestampCreated, timestampUpdated)
		
}
