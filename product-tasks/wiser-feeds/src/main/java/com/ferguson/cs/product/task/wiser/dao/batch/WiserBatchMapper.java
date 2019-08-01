package com.ferguson.cs.product.task.wiser.dao.batch;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.wiser.model.ProductDataHash;

@Mapper
public interface WiserBatchMapper {
	void deleteProductDataHash(ProductDataHash productDataHash);
	void insertProductDataHash(ProductDataHash productDataHash);
	void upsertProductDataHash(ProductDataHash productDataHash);
}
