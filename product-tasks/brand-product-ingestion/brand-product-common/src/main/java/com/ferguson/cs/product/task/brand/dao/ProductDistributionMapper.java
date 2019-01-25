package com.ferguson.cs.product.task.brand.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.brand.model.JsonReference;
import com.ferguson.cs.product.task.brand.model.ProductJson;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.SystemSource;



@Mapper
public interface ProductDistributionMapper {
	
	void upsertSystemSource(SystemSource systemSouirce);
	int getSystemSourceId(@Param("sourceName") String sourceName);
	void upsertProduct(BrandProduct product);
	int getProductId(@Param("productId") String productId,@Param("systemSourceId") Integer systemSourceId);
	void upsertJsonReferences(@Param("productId") Integer productId, @Param("jsonReferences") List<JsonReference> jsonReferences);
	
	List<ProductJson> listInactiveProducts(@Param("systemSourceId") Integer systemSourceId);
	
	void deleteInactiveProducts(@Param("systemSourceId") Integer systemSourceId, @Param("ids") List<Integer> ids);
	 
	void deleteInactiveProductJson( @Param("ids") List<Integer> ids);
	void deleteInactiveJson( @Param("ids") List<Integer> ids);
}
