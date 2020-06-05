package com.ferguson.cs.product.task.mpnmpidmismatch.model;

import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MpnMpidProductItem {
	private Integer uniqueId;
	private String productId;
	private String manufacturer;
	private String finish;
	private String sku;
	private String upc;
	private String mpn;
	private Integer mpid;
	private MdmProductView mpidProductView;
}
