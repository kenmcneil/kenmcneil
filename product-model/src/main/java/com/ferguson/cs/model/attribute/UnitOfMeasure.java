package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A unit of measure is standard unit that can be applied to a numerical attribute definition.
 * <p>
 * <b>EXAMPLE:</b>For a "length" attribute definition, the unit of measure might be in "inches", "centimeters", or "meters".
 * It is perfectly acceptable to have more than one length attribute (with different units of measure) associated to the
 * same product.
 *  <p>
 *   <b>NOTE:<b> It might be a good idea to have a "unit of measure type" enumeration added to this class (LENGTH, WEIGHT, SPEED, etc).   
 *   
 *   
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UnitOfMeasure implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Unique persistence ID.
	 */
	private String id;

	/**
	 * Unique business key.
	 */
	private String code;
	
	/**
	 * Name assigned to the unit of measure that is appropriate for display.  
	 */
	private String name;
	
	/**
	 * Description of unit of measure.
	 */
	private String description;
}
