package com.ferguson.cs.model.manufacturer;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ManufacturerCriteria {

	/**
	 * The persistent ID of the manufacturer used to limit the search
	 */
	private Integer manufacturerId;

	/**
	 * The name of the manufacturer. The name may include wild cards.
	 */
	private String manufacturerName;

}
