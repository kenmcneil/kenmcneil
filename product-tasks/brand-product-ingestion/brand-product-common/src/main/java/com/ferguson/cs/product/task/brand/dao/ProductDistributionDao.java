package com.ferguson.cs.product.task.brand.dao;

import java.util.List;

import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.SystemSource;



public interface ProductDistributionDao {
	 /**
	  * Update the record if sourceName is present. 
	  * Else insert new system source record. 
	  * 
	  * @param systemSource
	  */
	 void upsertSystemSource(SystemSource systemSource);
	 
	 /**
	  * Update the product if productid is present.
	  * Else insert new product record.
	  * 
	  * @param products
	  */
	 void upsertProducts(List<BrandProduct> products);
	 
	 /**
	  * To delete the inactive products i,e the produsts not created /updated with the job run
	  * 
	  * @param systemSourceId
	  */
	 void deleteStaleProducts(Integer systemSourceId);
	
}
