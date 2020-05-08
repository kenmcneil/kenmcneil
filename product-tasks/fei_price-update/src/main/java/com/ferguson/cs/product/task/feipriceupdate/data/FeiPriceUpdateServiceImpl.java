package com.ferguson.cs.product.task.feipriceupdate.data;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

@Service("feiPriceUpdateService")
public class FeiPriceUpdateServiceImpl implements FeiPriceUpdateService {
	
	private FeiPriceUpdateDao feiPriceUpdateDao;
	
	public FeiPriceUpdateServiceImpl(FeiPriceUpdateDao feiPriceUpdateDao) {
		this.feiPriceUpdateDao = feiPriceUpdateDao;
	}

	@Override
	public void createTempTable(String tempTableName) {
		feiPriceUpdateDao.createTempTable(tempTableName);
	}
	
	@Override
	public void dropTempTable(String tempTableName) {
		feiPriceUpdateDao.dropTempTable(tempTableName);
	}

	@Override
	public void insertTempPriceUpdateRecord(String tempTableName, FeiPriceUpdateItem item) {
		feiPriceUpdateDao.insertTempPriceUpdateRecord(tempTableName,item);
	}
	
	@Override
	public void insertTempFeiOnlyPriceUpdate(String tempTableName, FeiPriceUpdateItem item) {
		feiPriceUpdateDao.insertTempFeiOnlyPriceUpdate(tempTableName, item);
	}

}
