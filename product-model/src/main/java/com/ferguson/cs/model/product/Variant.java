package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ferguson.cs.model.image.ImageResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  A variant is a tangible unit of merchandise that has a specific name, part number, size, price, and any other attribute required to make the merchandise “sellable”.
 *
 *  A variant is associated with a parent product that acts as a
 *
 * @author tyler.vangorder
 *
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
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
