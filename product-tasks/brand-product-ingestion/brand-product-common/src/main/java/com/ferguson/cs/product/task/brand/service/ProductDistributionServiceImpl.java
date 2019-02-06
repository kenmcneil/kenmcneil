package com.ferguson.cs.product.task.brand.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.brand.dao.ProductDistributionDao;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.SystemSource;




@Service
public class ProductDistributionServiceImpl implements ProductDistributionService {

	@Autowired
	private ProductDistributionDao productDistributionDao;
	
	@Override
	public void saveSystemSource(SystemSource systemSource) {
		productDistributionDao.upsertSystemSource(systemSource);
	}
	
	@Override
	public void saveProducts(List<BrandProduct> products) {
		productDistributionDao.upsertProducts(products);
	}
	
	@Override
	public void deleteStaleProducts(Integer systemSourceId) {
		productDistributionDao.deleteStaleProducts(systemSourceId);
		
	}

}
