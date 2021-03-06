package com.ferguson.cs.model.product;

import com.ferguson.cs.utilities.IntMappedEnum;

/**
 * The "global" status of a product variant regardless of which channel the product is sold.
 * <p>
 * Please note that this is not a one-to-one mapping to the product status used at the channel level. A status of a product at the channel level
 * may have have its status impacted by the current inventory levels (stock, non-stock).
 *  <p>
 *  <table border="1">
 *  <tr><td>ACTIVE</td><td>The product is active and available to any channels associated with the product</td></tr>
 *  <tr><td>PENDING</td><td>The product is pending review by the product data team.</td></tr>
 *  <tr><td>NOT_APPROVED</td><td>The product has not been approved by the product data team</td></tr>
 *  <tr><td>DISCONTINUED</td><td>The product has been discontinued</td></tr>
 *  <tr><td>REMOVED</td><td>Product has been completed removed across all channels.</td></tr>
 *  </table>
 *
 * @author tyler.vangorder
 */
public enum ProductVariantStatus implements IntMappedEnum {

	ACTIVE(1),
	PENDING(2),
	NOT_APPROVED(3),
	DISCONTINUED(4),
	REMOVED(5);

	private int id;

	private ProductVariantStatus(int id) {
		this.id = id;
	}
	@Override
	public int getIntValue() {
		return id;
	}

}
