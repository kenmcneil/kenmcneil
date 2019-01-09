package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A unit of measure is standard unit that can be applied to a numerical attribute definition.
 * 
 * EXAMPLE: For a "length" attribute definition, the unit of measure might be in "inches", "centimeters", or "meters".
 * It is perfectly acceptable to have more than one length attribute (with different units of measure) associated to the
 * same product.
 *  
 *   NOTE: IT might be a good idea to have a unit category enumeration added to this class, however at this time if feels
 *   			  like overkill.
 *   
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UnitOfMeasure implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String code;
	private String description;
}
