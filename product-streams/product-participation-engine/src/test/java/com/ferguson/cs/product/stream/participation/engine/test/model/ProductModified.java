package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a record in the mmc.product.modified table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModified {
	private Integer uniqueId;
	private String modifiedBy;
	private Date modifiedDate;
}
