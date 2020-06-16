package com.ferguson.cs.product.task.mpnmpidmismatch.model;

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
	private Boolean mdmMpidMatch;
	private Boolean mdmMpnMatch;

	// MdmProductAttributes returned from MDM lookup on mpid
	private Long mdmMpidPrimaryVendorId;
	private String mdmMpidDescription;
	private String mdmMpidUpc;
	private String mdmMpidSku;
	private String mdmMpidAlternateCode;

	// MdmProductAttributes returned from MDM lookup on mpn
	private Long mdmMpnPrimaryVendorId;
	private String mdmMpnDescription;
	private String mdmMpnUpc;
	private String mdmMpnSku;
	private String mdmMpnAlternateCode;

}
