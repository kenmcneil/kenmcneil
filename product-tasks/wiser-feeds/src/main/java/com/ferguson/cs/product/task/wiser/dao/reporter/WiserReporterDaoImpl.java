package com.ferguson.cs.product.task.wiser.dao.reporter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

@Repository
public class WiserReporterDaoImpl implements WiserReporterDao {
	private WiserReporterMapper wiserReporterMapper;

	@Autowired
	public void setWiserReporterMapper(WiserReporterMapper wiserReporterMapper) {
		this.wiserReporterMapper = wiserReporterMapper;
	}

	@Override
	public Map<Integer, ProductRevenueCategory> getProductRevenueCategorization() {
		return wiserReporterMapper.getProductRevenueCategorization().stream().collect(Collectors.toMap(ProductRevenueCategory::getProductUniqueId, Function.identity()));
	}

	@Override
	public List<WiserSale> getParticipationProductSales(Date date) {
		return wiserReporterMapper.getParticipationProductSales(date);
	}

	@Override
	public Double getCurrentPrice(Integer uniqueId, Integer pricebookId) {
		return wiserReporterMapper.getCurrentPrice(uniqueId,pricebookId);
	}
}
