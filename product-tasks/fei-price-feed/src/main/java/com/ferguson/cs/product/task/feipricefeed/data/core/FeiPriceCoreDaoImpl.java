package com.ferguson.cs.product.task.feipricefeed.data.core;

import org.springframework.stereotype.Repository;

@Repository
public class FeiPriceCoreDaoImpl implements FeiPriceCoreDao {

	private final FeiPriceCoreMapper feiPriceCoreMapper;

	public FeiPriceCoreDaoImpl(FeiPriceCoreMapper feiPriceCoreMapper) {
		this.feiPriceCoreMapper = feiPriceCoreMapper;
	}

	public void deleteStalePromoFeiPriceData() {
		feiPriceCoreMapper.deleteStalePromoFeiPriceData();
	}
}
