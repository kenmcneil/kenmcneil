package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

@Mapper
public interface ProductMapper {
	List<Product> getQuickShipEligibleProduct(@Param("criteria") QuickshipEligibleProductSearchCriteria criteria);

	List<ProductLeadTimeOverrideRule> getProductLeadTimeOverrideRule(
			@Param("criteria") ProductLeadTimeOverrideRuleSearchCriteria criteria);
}
