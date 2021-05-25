package com.ferguson.cs.product.task.omnipriceharmonization.service;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.omnipriceharmonization.data.reporter.OmniPriceHarmonizationDao;

@Service
public class OmniPriceHarmonizationServiceImpl implements OmniPriceHarmonizationService {

	private final OmniPriceHarmonizationDao feiPriceDao;

	public OmniPriceHarmonizationServiceImpl(OmniPriceHarmonizationDao feiPriceDao) {
		this.feiPriceDao = feiPriceDao;

	}

	@Override
	public void truncatePriceHarmonizationData() {
		feiPriceDao.truncatePriceHarmonizationData();
	}
}
