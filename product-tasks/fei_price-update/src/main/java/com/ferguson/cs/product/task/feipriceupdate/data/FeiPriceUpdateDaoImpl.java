package com.ferguson.cs.product.task.feipriceupdate.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.util.Assert;

import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceJobStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductSyncJob;

public class FeiPriceUpdateDaoImpl implements FeiPriceUpdateDao {
	
	private FeiPriceUpdateMapper feiPriceUpdateMapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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
	
	@Override
	public void insertCostUpdateJob(CostUpdateJob costUpdateJob) {
		Assert.notNull(costUpdateJob, "Unable to insert cost update job, cost price update object is null.");
		Assert.notNull(costUpdateJob.getStatus(), "Unable to insert cost update job, cost update Status is null.");
		Assert.notNull(costUpdateJob.getProcessOn(), "Unable to insert cost update job, cost update Process On is null.");
		feiPriceUpdateMapper.insertCostUpdateJob(costUpdateJob);
	}
	
	public void insertPriceBookCostUpdates(PriceBookSync priceBookSync) {
		feiPriceUpdateMapper.insertPriceBookCostUpdates(priceBookSync);
	}
}

