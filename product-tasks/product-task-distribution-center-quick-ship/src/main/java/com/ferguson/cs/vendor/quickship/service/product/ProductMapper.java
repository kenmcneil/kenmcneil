package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

@Mapper
public interface ProductMapper {
	List<Product> getQuickShipEligibleProduct(@Param("criteria") QuickshipEligibleProductSearchCriteria criteria);

	public void updateProductModified(Product product);

	/**
	 * Clears the data from the MMC.dbo.ProductPreferredVendorQuickShip table, in
	 * preparation of being populated by the method below.
	 */
	void truncateProductPreferredVendorQuickShip();

	/**
	 * Copies MMC.dbo.ProductPreferredVendor table contents into ProductPreferredVendorQuickShip.
	 */
	void copyProductPreferredVendorTableForQuickShip();
}
