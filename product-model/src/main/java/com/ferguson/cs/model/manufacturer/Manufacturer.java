package com.ferguson.cs.model.manufacturer;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.ferguson.cs.model.Auditable;

import lombok.Builder;
import lombok.Data;

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
@Builder
@Data
public class Manufacturer implements Auditable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistent ID
	 */
	@Id
	private Long id;

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

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;
	private Long version;

}
