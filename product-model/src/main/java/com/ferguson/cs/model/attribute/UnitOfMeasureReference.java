package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import org.springframework.data.annotation.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A unit of measure reference can be nested in other entities and represents a "read-only" view of the unit of measure.
 *
 * @author tyler.vangorder
 */
@ToString
@Setter
@Getter
public class UnitOfMeasureReference implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistence ID.
	 */
	private Integer id;

	/**
	 * Unique business key.
	 */
	@Transient
	private String code;

	/**
	 * Name assigned to the unit of measure that is appropriate for display.
	 */
	@Transient
	private String name;

	/**
	 * Description of unit of measure.
	 */
	@Transient
	private String description;

}
