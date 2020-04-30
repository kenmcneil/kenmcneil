package com.ferguson.cs.product.task.feipricefeed.batch;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

public class FeiPriceDataMapItemWriter extends AbstractItemStreamItemWriter<FeiPriceData> {
	private final Map<String, FeiPriceData> feiPriceDataMap;
	private FeiPriceSettings feiPriceSettings;
	private FeiPriceDataComparator feiPriceDataComparator = new FeiPriceDataComparator();
	private Set<FeiPriceData> duplicateData;

	public FeiPriceDataMapItemWriter(Map<String, FeiPriceData> feiPriceDataMap) {
		this.feiPriceDataMap = feiPriceDataMap;
	}

	@Autowired
	public void setFeiPriceSettings(FeiPriceSettings feiPriceSettings) {
		this.feiPriceSettings = feiPriceSettings;
	}

	@Autowired
	public void setDuplicateData(Set<FeiPriceData> duplicateData) {
		this.duplicateData = duplicateData;
	}

	@Override
	public void write(List<? extends FeiPriceData> items) throws Exception {
		for(FeiPriceData item : items) {
			if(!feiPriceDataMap.containsKey(item.getMpn()) || feiPriceDataComparator.compare(item,feiPriceDataMap.get(item.getMpn())) > 0) {
				feiPriceDataMap.put(item.getMpn(),item);
			}
		}
	}

	private class FeiPriceDataComparator implements Comparator<FeiPriceData> {

		@Override
		public int compare(FeiPriceData o1, FeiPriceData o2) {
			if (o1 == null && o2 == null) {
					return 0;
				}

				//Prioritize existing objects over non-existent ones
				if (o1 == null) {
					return -1;
				}

				if (o2 == null) {
					return 1;
				}

				if (o1.equals(o2)) {
					return 0;
				}

				//Prefer stock products to nonstock
				if (o1.getStatus().equalsIgnoreCase("stock") && !o2.getStatus().equalsIgnoreCase("stock")) {
					return 1;
				} else if (!o1.getStatus().equalsIgnoreCase(o2.getStatus())) {
					return -1;
				}

				//Prefer non-whitelabel to whitelabel
				if (feiPriceSettings != null && !CollectionUtils.isEmpty(feiPriceSettings.getWhiteLabelBrands())) {
					if (feiPriceSettings.getWhiteLabelBrands().contains(o1.getBrand()) && !feiPriceSettings
							.getWhiteLabelBrands().contains(o2.getBrand())) {
						return -1;
					} else if (feiPriceSettings.getWhiteLabelBrands().contains(o2.getBrand())) {
						return 1;
					}
				}

				//Doesn't meet any of the criteria to pick a higher priority
				if(BigDecimal.valueOf(o1.getPrice()).compareTo(BigDecimal.valueOf(o2.getPrice())) != 0) {
					duplicateData.add(o1);
					duplicateData.add(o2);
				}
				return 0;
			};
		}
}
