package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.model.image.ImageResource;
import com.ferguson.cs.model.manufacturer.Manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A product representations a family of closely related items called "variants" and defines all of the characteristics/attributes that are pertinent
 * to the product and its variants. Information that is common across the variants is represented at the product level while each unique variant will have
 * a slightly different configuration of the product's characteristics.
 * <p>
 * <b>Example:</b>
 *<p>
 * A user that is browsing a web store front selects a product page for "Delta 551-DST" faucet and the product page provides details
 * common to the faucet. Additionally, the faucet is rendered with six different variants, each with a different color.
 * The variant represents a "sellable" item and user selects the variant, "Brilliance Polished Nickel" (Delta 551-PN-DST) to add to their cart.
 * <p>
 * <b>NOTE:</b> The manufacturer and the manufacturer's product ID are enough to identify a product/family but that is not enough to identify
 *  				   a specific, sellable item.
 *
 * @author tyler.vangorder
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistence ID.
	 */
	private String id;

	/**
	 * The product title
	 */
	private String title;

	/**
	 * The description of the product. There must not be any styling embedded in the description.
	 */
	private String description;

	//TODO: Need to define how to better break up the description into separate, smaller fields. Need input from data team, supply, and Dan V.


	/**
	 * The product ID assigned to the product family by the manufacturer.
	 */
	private String manufacturerProductId;

	/**
	 * The manufacture of the product.
	 */
	private Manufacturer manufacturer;

	/**
	 * Often a manufacturer with product a family of products and associate them with a product series. Example: Kohler may have several products
	 * that belong to the "Iron Tones" series that includes a common theme for towel racks, faucets, and bathtub hardware.
	 */
	private ProductSeries series;

	/**
	 * The collection type indicates if the product represents an individual item, a collection of items that are sold together but are also individually sold,
	 * or a collection of items that are sold together and NOT individually sold.
	 *
	 *   TODO: Need to figure out if this is the best way to handle bundles and kits.
	 */
	private ProductCollectionType collectionType;

	/**
	 * A list of characteristics that are used to describe the details of the product family. Attributes at the product level can be assigned values (and marked
	 * as "final") or they can be used as a template and then assigned values within each variant.
	 */
	private List<ProductAttribute> attributeList;


	private List<Variant> variantList;

	/**
	 * A collection of image assets that are associated with a product.
	 */
	private List<ImageResource> imageList;

	//TODO Need to figure out what to do with auditing columns (timestampCreated, timestampUpdated)

}
