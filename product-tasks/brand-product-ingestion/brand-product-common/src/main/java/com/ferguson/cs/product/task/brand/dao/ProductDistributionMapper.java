package com.ferguson.cs.product.task.brand.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.JsonReference;
import com.ferguson.cs.product.task.brand.model.ProductJson;
import com.ferguson.cs.product.task.brand.model.SystemSource;



@Mapper
public interface ProductDistributionMapper {
	
	Integer getSystemSourceId(String sourceName);
	
	void insertSystemSource(SystemSource systemSource);
	
	void updateSystemSource(SystemSource systemSource);
	
	
	Integer getProductId(@Param("productId") String productId,@Param("systemSourceId") Integer systemSourceId);
	
	void insertProduct(BrandProduct product);
	
	void updateProduct(BrandProduct product);
	
	
	
	Integer getJson(@Param("jsonTypeId") Integer jsonTypeId,@Param("productId") Integer productId);
	
	void insertJson(JsonReference jsonReference);
	
	void updateJson(@Param("id") Integer id, @Param("jsonString") String jsonString );
	
	void insertProductJson( @Param("productId") Integer productId , @Param("jsonId") Integer jsonId);
	
	
	List<ProductJson> listStaleProducts(@Param("systemSourceId") Integer systemSourceId);
	
	void deleteProducts(@Param("systemSourceId") Integer systemSourceId, @Param("ids") List<Integer> ids);
	 
	void deleteProductJson(@Param("ids") List<Integer> ids);
	
	void deleteJson(@Param("ids") List<Integer> ids);
}
