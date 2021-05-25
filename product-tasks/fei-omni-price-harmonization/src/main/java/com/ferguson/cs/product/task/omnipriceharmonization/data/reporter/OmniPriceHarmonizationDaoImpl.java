package com.ferguson.cs.product.task.omnipriceharmonization.data.reporter;

import org.springframework.stereotype.Repository;

@Repository
public class OmniPriceHarmonizationDaoImpl implements OmniPriceHarmonizationDao {

	private final OmniPriceHarmonizationMapper omniPriceHarmonizationMapper;

	public OmniPriceHarmonizationDaoImpl(OmniPriceHarmonizationMapper omniPriceHarmonizationMapper) {
		this.omniPriceHarmonizationMapper = omniPriceHarmonizationMapper;
	}

	@Override
	public void truncatePriceHarmonizationData() {
		omniPriceHarmonizationMapper.truncatePriceHarmonizationData();
	}
}
