package com.ferguson.cs.product.task.wiser.dao.core;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WiserMapper {
	void populateProductRevenueCategorization();
}
