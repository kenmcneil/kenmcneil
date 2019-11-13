package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.model.attribute.AttributeDefinitionReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A product option represents a "customization" that can be made to a product that does NOT change the variant's SKU. The list of options must be
 * selected by the user when they are adding an item to a cart or project. Some of the customizations may impact the price of the item and the price
 * of each option value is defined the nested "ProductOptionValue" model.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * Each product option must have a link to an attribute definition. The definition sets the datatype and the validation
	 * rules that should be applied to the option values.
	 */
	private AttributeDefinitionReference definition;

	/**
	 * An option can define a finite number of values that can be selected by the user when adding the product to a cart/project.
	 * If the optionValues are not defined, the user may enter a value that conforms to the validation rules defined by the attribute definition.
	 * If the optionValues are defined, the user may only select one of the given option values.
	 */
	private List<ProductOptionValue> optionValues;
}
