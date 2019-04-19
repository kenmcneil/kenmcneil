package com.ferguson.cs.product.task.supply.dao;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ferguson.cs.product.task.supply.model.Product;
import com.ferguson.cs.product.task.supply.model.Vendor;

@Mapper
public interface ProductDataFeedMapper {
	List<Product> getProductData(Date lastRanDate);
	List<Vendor> getVendorData();
}
