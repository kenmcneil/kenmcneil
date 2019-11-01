package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import org.springframework.data.annotation.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A unit of measure reference can be nested in other entities and represents a "read-only" view of the unit of measure.
 *
 * @author tyler.vangorder
 */
@Data
@NoArgsConstructor
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

	public UnitOfMeasureReference(UnitOfMeasure unitOfMeasure) {
		this.id = unitOfMeasure.getId();
		this.code = unitOfMeasure.getCode();
		this.name = unitOfMeasure.getName();
		this.description = unitOfMeasure.getDescription();
	}
}
