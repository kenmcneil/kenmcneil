package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateItemWriter.class);

	private final FeiPriceUpdateService feiPriceUpdateService;

	public FeiPriceUpdateItemWriter(
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {

		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>) items) {

			LOGGER.debug("FeiPriceUpdateItemWriter - Processing Item uniqueId: {}, mpid:{}, consumer price: {}",
					item.getUniqueId(), item.getMpid(), item.getPrice());

			feiPriceUpdateService.insertTempPriceUpdateRecord(item);
		}
	}

}
