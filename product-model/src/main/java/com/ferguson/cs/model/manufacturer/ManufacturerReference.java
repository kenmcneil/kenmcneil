package com.ferguson.cs.model.manufacturer;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

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
 * @author tyler.vangorder
 */
@ToString
@Setter
@Getter
public class ManufacturerReference implements Serializable {

	private static final long serialVersionUID = 1L;

	public ManufacturerReference() {
	}

	public ManufacturerReference(Manufacturer manufacturer) {
		this.id = manufacturer.getId();
		this.name = manufacturer.getName();
		this.description = manufacturer.getDescription();
	}

	/**
	 * Unique persistent ID
	 */
	@Id
	private Integer id;

	/**
	 * Manufacturer name
	 */
	private String name;

	/**
	 * Detailed description about the manufacturer
	 */
	private String description;

}
