package com.ferguson.cs.product.task.feipriceupdate.data;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceJobStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;

@Service("feiPriceUpdateService")
public class FeiPriceUpdateServiceImpl implements FeiPriceUpdateService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateServiceImpl.class);
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
	public CostUpdateJob createCostUploadJob(String fileName, CostPriceType type, Date processOn, Integer userId) {
		Assert.notNull(fileName, "Unable to create cost update job, file name is null.");
		Assert.notNull(type, "Unable to create cost update job, Cost Price Type object is null.");
		Assert.notNull(processOn, "Unable to create cost update job, processOn date is null.");
		Assert.notNull(userId, "Unable to create cost update job, User ID is null.");
		
		//Create Update job: LOADING Status
		CostUpdateJob costUpdateJob = new CostUpdateJob();
		costUpdateJob.setProcessType(type.toString());
		costUpdateJob.setJobName(fileName);
		costUpdateJob.setUserId(userId);
		costUpdateJob.setProcessOn(processOn);
		costUpdateJob.setStatus(CostPriceJobStatus.LOADING.toString());

		feiPriceUpdateDao.insertCostUpdateJob(costUpdateJob);
		return costUpdateJob;
	}
	
	@Override
	public void insertPriceBookCostUpdates(PriceBookSync priceBookSync) {
		feiPriceUpdateDao.insertPriceBookCostUpdates(priceBookSync);
	}
	
	@Override
	public FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item) {
		return feiPriceUpdateDao.getPriceUpdateProductDetails(item);
	}

	@Override
	public void loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria) {
		feiPriceUpdateDao.loadPriceBookCostUpdatesFromTempTable(priceBookLoadCriteria);
	}
		
}
