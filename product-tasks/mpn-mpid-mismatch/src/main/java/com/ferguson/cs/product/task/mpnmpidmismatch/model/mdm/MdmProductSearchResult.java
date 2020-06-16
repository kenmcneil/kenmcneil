package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmProductSearchResult implements Serializable {

	private static final long serialVersionUID = 2L;

	private Integer id;
	private Integer productSearchId;
	private Long mpId;
	private String upc;
	private String sku;
	private String masterProductName;
	private Long primaryVendorId;
	private String primaryVendorName;
	private Double searchScore;
}
