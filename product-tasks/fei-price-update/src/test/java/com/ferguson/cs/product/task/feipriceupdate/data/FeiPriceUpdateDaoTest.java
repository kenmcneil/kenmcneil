package com.ferguson.cs.product.task.feipriceupdate.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipriceupdate.BaseFeiPriceUpdateTest;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceUpdateStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.PricebookType;

public class FeiPriceUpdateDaoTest extends BaseFeiPriceUpdateTest {

	@Autowired
	private FeiPriceUpdateDao feiPriceUpdateDao;

	@Test
	public void test_getPb1PriceUpdateProductDetails() {

		Integer uniqueId = jdbcTemplate.queryForObject("select top 1 uniqueId from mmc.dbo.pricebook_cost where pricebookId = 1", Integer.class);

		FeiPriceUpdateItem item = new FeiPriceUpdateItem();
		item.setUniqueId(uniqueId);
		item.setPricebookId(1);

		FeiPriceUpdateItem resultItem = feiPriceUpdateDao.getPb1PriceUpdateProductDetails(item);
		assertThat(resultItem).isNotNull();
	}

	@Test
	public void test_getPb22PriceUpdateProductDetails() {

		// PB22 joins to our temp table created by the PB1 processing step which runs
		// first.  Need to create a temp table.  Table name here is not what is used in prod.
		// Name does not matter as we pass it as a param.
		feiPriceUpdateDao.createTempTable("feiPriceUpdate_test");

		Integer uniqueId = jdbcTemplate.queryForObject("select top 1 uniqueId from mmc.dbo.pricebook_cost where pricebookId = 22", Integer.class);

		FeiPriceUpdateItem item = new FeiPriceUpdateItem();
		Double newPrice = new Double(100);
		item.setUniqueId(uniqueId);
		item.setPricebookId(PricebookType.PB1.getIntValue());
		item.setMpid(1);
		item.setPrice(newPrice);
		item.setPreferredVendorCost(90.00);
		item.setPriceUpdateStatus(PriceUpdateStatus.VALID);
		item.setMargin(10.00);
		item.setTempTableName("feiPriceUpdate_test");

		feiPriceUpdateDao.insertTempPriceUpdateRecord(item);

		FeiPriceUpdateItem resultItem = feiPriceUpdateDao.getPb22PriceUpdateProductDetails(item);
		assertThat(resultItem).isNotNull();
		assertThat(resultItem.getNewPb1Price()).isNotNull();
		assertThat(resultItem.getNewPb1Price()).isEqualByComparingTo(newPrice);
		assertThat(resultItem.getExistingPb1Price()).isNotNull();
		feiPriceUpdateDao.dropTempTable("feiPriceUpdate_test");
	}

}
