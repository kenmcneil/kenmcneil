package com.ferguson.cs.product.task.wiser.dao.core;

import org.springframework.stereotype.Repository;

@Repository
public class WiserDaoImpl implements WiserDao {

	private final WiserMapper wiserMapper;

	public WiserDaoImpl(WiserMapper wiserMapper) {
		this.wiserMapper = wiserMapper;
	}

	@Override
	public void populateProductRevenueCategorization() {
		wiserMapper.populateProductRevenueCategorization();
	}
}
