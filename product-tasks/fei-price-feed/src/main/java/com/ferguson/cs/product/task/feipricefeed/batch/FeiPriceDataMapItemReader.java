package com.ferguson.cs.product.task.feipricefeed.batch;

import java.util.Iterator;
import java.util.Map;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

public class FeiPriceDataMapItemReader extends AbstractItemStreamItemReader<FeiPriceData> {

	private final Map<String,FeiPriceData> feiPriceDataMap;
	private FeiPriceSettings feiPriceSettings;

	public FeiPriceDataMapItemReader(Map<String, FeiPriceData> feiPriceDataMap) {
		this.feiPriceDataMap = feiPriceDataMap;
	}

	@Autowired
	public void setFeiPriceSettings(FeiPriceSettings feiPriceSettings) {
		this.feiPriceSettings = feiPriceSettings;
	}

	@Override
	public FeiPriceData read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Iterator<String> iterator = feiPriceDataMap.keySet().iterator();

		if (iterator.hasNext()) {
			return feiPriceDataMap.remove(iterator.next());
		}
		return null;
	}
}
