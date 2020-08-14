package com.ferguson.cs.product.task.wiser.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.wiser.dao.core.WiserDao;
import com.ferguson.cs.product.task.wiser.dao.integration.WiserIntegrationDao;
import com.ferguson.cs.product.task.wiser.dao.reporter.WiserReporterDao;
import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.RecommendationJobLog;
import com.ferguson.cs.product.task.wiser.model.UniqueIdPricebookIdTuple;
import com.ferguson.cs.product.task.wiser.model.WiserSale;
import com.ferguson.cs.task.batch.util.JobRepositoryHelper;
import com.ferguson.cs.utilities.DateUtils;

@Service
public class WiserServiceImpl implements WiserService {
	private WiserIntegrationDao wiserIntegrationDao;
	private WiserReporterDao wiserReporterDao;
	private WiserDao wiserDao;
	private JobRepositoryHelper jobRepositoryHelper;

	@Autowired
	public void setWiserIntegrationDao(WiserIntegrationDao wiserIntegrationDao) {
		this.wiserIntegrationDao = wiserIntegrationDao;
	}

	@Autowired
	public void setWiserReporterDao(WiserReporterDao wiserReporterDao) {
		this.wiserReporterDao = wiserReporterDao;
	}

	@Autowired
	public void setWiserDao(WiserDao wiserDao) {
		this.wiserDao = wiserDao;
	}

	@Autowired
	public void setJobRepositoryHelper(JobRepositoryHelper jobRepositoryHelper) {
		this.jobRepositoryHelper = jobRepositoryHelper;
	}

	@Override
	public Date getLastRanDate(String jobName) {
		JobExecution jobExecution = jobRepositoryHelper.getLastJobExecution(jobName, ExitStatus.COMPLETED);
		if(jobExecution != null) {
			return jobExecution.getEndTime();
		}
		return  null;
	}

	@Override
	public List<ProductDataHash> getAllProductDataHashes() {
		return wiserIntegrationDao.getAllProductDataHashes();
	}

	@Override
	public List<WiserSale> getWiserSales(Date date) {
		List<WiserSale> sales = wiserIntegrationDao.getActiveOrModifiedWiserSales(date);
		sales.addAll(wiserReporterDao.getParticipationProductSales(date));
		return sales;
	}

	@Override
	public Map<Integer, ProductRevenueCategory> getProductRevenueCategorization() {
		return wiserReporterDao.getProductRevenueCategorization();
	}

	@Override
	public Map<Integer, ProductConversionBucket> getProductConversionBuckets() {
		return wiserIntegrationDao.getProductConversionBuckets();
	}

	@Override
	public String getProductConversionBucket(Integer productUniqueId) {
		return wiserIntegrationDao.getProductConversionBucket(productUniqueId).getConversionBucket().getStringValue();
	}

	@Override
	public boolean isItemPromo(WiserSale wiserSale, Date date) {
		//If there is no sale object, or if any relevant dates aren't supplied, assume no promo
		if (wiserSale == null || date == null || wiserSale.getStartDate() == null || wiserSale
				.getEndDate() == null) {
			return false;
		}

		LocalDate endDate = DateUtils.today().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate startDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate saleStartDate = wiserSale.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate saleEndDate = wiserSale.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		//Start date should not be after end date
		if (ChronoUnit.DAYS.between(startDate, endDate) < 0) {
			return false;
		}


		// If product is currently on sale, sale start date should be today or earlier, end date of range should be
		// before end of sale
		return ChronoUnit.DAYS.between(saleStartDate, endDate) >= 0 && ChronoUnit.DAYS
				.between(endDate, saleEndDate) >= 0;
	}

	@Override
	public void truncateProductDataHashes() {
		wiserIntegrationDao.truncateProductDataHashes();
	}

	@Override
	public void populateProductRevenueCategorization() {
		wiserDao.populateProductRevenueCategorization();
	}

	@Override
	public Double getCurrentPrice(Integer uniqueId, Integer pricebookId) {
		return wiserReporterDao.getCurrentPrice(uniqueId,pricebookId);
	}

	@Override
	public Map<UniqueIdPricebookIdTuple, Double> getCurrentPriceData(List<Integer> uniqueIds, int partitionSize) {
		return wiserReporterDao.getCurrentPriceData(uniqueIds,partitionSize);
	}

	@Override
	public void insertRecommendationJobLog(RecommendationJobLog recommendationJobLog) {
		wiserIntegrationDao.insertRecommendationJobLog(recommendationJobLog);
	}

	@Override
	public void deleteTodaysRecommendationJobLogs() {
		Date startOfToday = DateUtils.today();
		wiserIntegrationDao.deleteRecommendationJobLogsAfterDateTime(startOfToday);
	}


}
