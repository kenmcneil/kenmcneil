package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DuplicateKeyException;

import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceUpdateStatus;

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

			// Added unique constraint on temp table based on uniqueId and pricebookId.  This will
			// catch duplicate input records being sent.  If we catch the exception I am updating
			// the existing record in the table with an error status.  We don't want it processed by
			// the cost updater.  This will also cause it to end up on the error report.
			try {
				feiPriceUpdateService.insertTempPriceUpdateRecord(item);
			} catch (DuplicateKeyException ex) {

				LOGGER.error("FeiPriceUpdateItemWriter - Duplicate input record, uniqueId: {}, mpid:{}, consumer price: {}",
						item.getUniqueId(), item.getMpid(), item.getPrice());

				// Update the existing record with an error status.  We don't know which one is valid.
				item.setPriceUpdateStatus(PriceUpdateStatus.DUPLICATE_INPUT_RECORD_ERROR);
				item.setStatusMsg("DUPLICATE_INPUT_RECORD_ERROR - Duplicate input records exist for uniqueId");
				feiPriceUpdateService.updateTempPriceUpdateRecordStatus(item);
			}
		}
	}
}
