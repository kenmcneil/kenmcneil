package com.ferguson.cs.product.task.feipriceupdate.data;

import org.springframework.util.Assert;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateDaoImpl implements FeiPriceUpdateDao {
	
	private FeiPriceUpdateMapper feiPriceUpdateMapper;
	
	public FeiPriceUpdateDaoImpl(FeiPriceUpdateMapper feiPriceUpdateMapper) {
		this.feiPriceUpdateMapper = feiPriceUpdateMapper;
	}

	public void createTempTable(String tempTableName) {
		feiPriceUpdateMapper.createTempTable(tempTableName);
	}

	@Override
	public void dropTempTable(String tempTableName) {
		Assert.notNull(tempTableName);
		feiPriceUpdateMapper.dropTempTable(tempTableName);
		
	}

	@Override
	public void insertTempPriceUpdateRecord(String tempTableName, FeiPriceUpdateItem item) {
		feiPriceUpdateMapper.insertTempPriceUpdateRecord(tempTableName, item);
		
	}
	
	@Override
	public void insertTempFeiOnlyPriceUpdate(String tempTableName, FeiPriceUpdateItem item) {
		feiPriceUpdateMapper.insertTempFeiOnlyPriceUpdate(tempTableName, item);		
	}
}

