package com.ferguson.cs.product.task.feipriceupdate.data;

import org.springframework.util.Assert;

import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;


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
	public void insertTempPriceUpdateRecord(FeiPriceUpdateItem item) {
		feiPriceUpdateMapper.insertTempPriceUpdateRecord(item);

	}

	@Override
	public FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item) {
		return feiPriceUpdateMapper.getPriceUpdateProductDetails(item);
	}

	@Override
	public Integer loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria) {
		return feiPriceUpdateMapper.loadPriceBookCostUpdatesFromTempTable(priceBookLoadCriteria);
	}

	@Override
	public void executePriceBookCostUpdater(Integer costUpdateJobId) {
		Assert.notNull(costUpdateJobId, "Unable to execute PriceBook Cost Update Job due to null costUpdateJobId.");
		feiPriceUpdateMapper.executePricebookUpdater(costUpdateJobId);
	}

	@Override
	public void insertCostUpdateJob(CostUpdateJob costUpdateJob) {
		Assert.notNull(costUpdateJob, "Unable to insert cost update job, cost price update object is null.");
		Assert.notNull(costUpdateJob.getStatus(), "Unable to insert cost update job, cost update Status is null.");
		Assert.notNull(costUpdateJob.getProcessOn(),
				"Unable to insert cost update job, cost update Process On is null.");
		feiPriceUpdateMapper.insertCostUpdateJob(costUpdateJob);
	}

	@Override
	public void updateCostUpdateJob(CostUpdateJob costUpdateJob) {
		Assert.notNull(costUpdateJob, "Unable to update cost update job, cost price update object is null.");
		Assert.notNull(costUpdateJob.getId(), "Unable to update cost update job, costUpdateJobId is null.");
		Assert.notNull(costUpdateJob.getStatus(), "Unable to update cost update job, cost update Status is null.");
		Assert.notNull(costUpdateJob.getProcessOn(),
				"Unable to update cost update job, cost update Process On is null.");
		feiPriceUpdateMapper.updateCostUpdateJob(costUpdateJob);
	}

	@Override
	public CostUpdateJob getCostUpdateJob(Integer costUpdateJobId) {
		Assert.notNull(costUpdateJobId, "Unable to retrieve cost update job due to null costUpdateJobId.");
		return feiPriceUpdateMapper.getCostUpdateJob(costUpdateJobId);
	}
}
