package com.ferguson.cs.model.manufacturer;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A manufacturer represents an entity that creates/builds products that are sold through the various sales channels.
 *
 * It is important to distinguish the difference between a manufacturer and a vendor. A manufacturer is the "maker" of the
 * product whereas the vendor is the distributor/reseller of those products. A product has one manufacturer but may have
 * multiple vendors that distribute and sell that product.
 *
 * TODO - This is a minimal representation of the manufacturer, need to flesh out what else should be defined for a manufacturer.
 *
 * @author tyler.vangorder
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Manufacturer implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistent ID
	 */
	private String id;

	/**
	 * Manufacturer name
	 */
	private String name;

	/**
	 * Detailed description about the manufacturer
	 */
	private String description;

	/**
	 * Is the manufacturer active
	 */
	private boolean active;

}
