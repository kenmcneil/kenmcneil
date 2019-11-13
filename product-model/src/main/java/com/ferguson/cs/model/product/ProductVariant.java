package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Version;

import com.ferguson.cs.model.Auditable;
import com.ferguson.cs.model.asset.DigitalResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A variant is a tangible unit of merchandise that has a specific name, part number, size, price, and any other attribute required to make the merchandise “sellable”.
 *
 * A product is uniquely identifiable via Ferguson's MPN ID and this ID is assigned from Ferguson master product data system. Additionally, a variant can also be
 * identified by one of its alternate Variant Identifiers which consist of a type (GTIN, SKU, UPC, etc) and the actual identifier value.
 *
 * @author tyler.vangorder
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant implements Auditable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistence ID.
	 */
	private Integer id;

	/**
	 *  A reference to the parent product that this variant belongs.
	 */
	private ProductReference productReference;

	/**
	 * The description of the variant. There must not be any styling embedded in the description.
	 */
	private String description;

	//TODO: Need to define how to better break up the description into separate, smaller fields. Need input from data team, supply, and Dan V.

	/**
	 * Each variant is assigned a status to represent the different stages in the variant data's life cycle.
	 */
	private ProductVariantStatus status;

	/**
	 * The manufacturer's suggested retail price.
	 */
	private BigDecimal msrp;

	/**
	 * A list of alternate IDs that can be used to reference the product variant.
	 */
	private List<ProductVariantIdentifier> identifierList;

	/**
	 * A collection of product characteristics that differentiate this variant from the other variants within the same product family.
	 */
	private List<ProductVariantAttribute> attributeList;

	/**
	 * The weight of the variant in pounds?
	 */
	private BigDecimal weight;

	/**
	 * The estimated cost of shipping this variant via freight.
	 *
	 * TODO: NOT SURE THIS BELONGS HERE, as this is more an attribute that should be related to vendor and shipping method.
	 */
	private BigDecimal freightCost;

	/**
	 * Variant's handling fee.
	 *
	 * TODO: What is a handling fee? Is there a use case/user story for this?
	 */
	private BigDecimal handlingFee;

	/**
	 * Variant's handling fee by item.
	 *
	 * TODO: What is a handling fee? Is there a use case/user story for this? What is the difference between this and handling fee?
	 */
	private BigDecimal handlingFeeByItem;


	/**
	 * Variant's drop ship fee
	 *
	 * TODO: What is a drop ship fee and does this really belong here?
	 */
	private BigDecimal dropShipFee;

	/**
	 * Is there free shipping offered for this variant?
	 *
	 * TODO: This doesn't feel like something that should be recorded at this level, but rather at the vendor and/or channel level.
	 */
	private Boolean isFreeShipping;

	/**
	 * Must this variant be shipped via freight?
	 */
	private Boolean isFreight;

	/**
	 * Can this variant be shipped at all?
	 */
	private Boolean isShippable;

	/**
	 * Can this variant be shipped outside of the United States?
	 */
	private Boolean isShippableToForeignCountry;

	/**
	 * A collection of digital assets that are associated with a product. The can be images, documents, or AR models.
	 */
	private List<DigitalResource> digitalResourceList;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;

	@Version
	private Integer version;


}
