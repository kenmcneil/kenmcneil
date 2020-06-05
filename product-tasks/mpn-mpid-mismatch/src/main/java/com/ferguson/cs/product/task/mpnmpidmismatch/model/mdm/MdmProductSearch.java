package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmProductSearch implements Serializable {
	private static final long serialVersionUID = 4L;

	private Integer id;
	private Integer productSearchJobId;
	private Long primaryVendorId;
	private String primaryVendorName;
	private String productName;
	private String upc;
	private String sku;
	private String productDescription;
	private List<String> errorMessages;
}
