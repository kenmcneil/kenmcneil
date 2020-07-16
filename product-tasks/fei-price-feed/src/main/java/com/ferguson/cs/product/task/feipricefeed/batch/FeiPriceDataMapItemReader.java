package com.ferguson.cs.product.task.feipricefeed.batch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

public class FeiPriceDataMapItemReader extends AbstractItemStreamItemReader<FeiPriceData> {

	private final Map<String, List<FeiPriceData>> feiPriceDataMap;

	public FeiPriceDataMapItemReader(Map<String, List<FeiPriceData>> feiPriceDataMap) {
		this.feiPriceDataMap = feiPriceDataMap;
	}

	@Override
	public FeiPriceData read() {
		Iterator<String> iterator = feiPriceDataMap.keySet().iterator();

		while(iterator.hasNext()) {
			List<FeiPriceData> list = feiPriceDataMap.get(iterator.next());
			if(CollectionUtils.isEmpty(list)) {
				iterator.remove();
				continue;
			}
			return list.remove(0);
		}
		return null;
	}
}
