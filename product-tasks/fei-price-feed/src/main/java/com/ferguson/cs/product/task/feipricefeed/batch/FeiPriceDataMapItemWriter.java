package com.ferguson.cs.product.task.feipricefeed.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipricefeed.model.DeprioritizedBrandView;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceDataStatus;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;

public class FeiPriceDataMapItemWriter extends AbstractItemStreamItemWriter<FeiPriceData> {
	private final Map<String, List<FeiPriceData>> feiPriceDataMap;
	private final FeiPriceService feiPriceService;
	private FeiPriceDataComparator feiPriceDataComparator = new FeiPriceDataComparator();
	private Set<FeiPriceData> duplicateData;

	public FeiPriceDataMapItemWriter(Map<String, List<FeiPriceData>> feiPriceDataMap, FeiPriceService feiPriceService) {
		this.feiPriceDataMap = feiPriceDataMap;
		this.feiPriceService = feiPriceService;
	}


	@Autowired
	public void setDuplicateData(Set<FeiPriceData> duplicateData) {
		this.duplicateData = duplicateData;
	}

	@Override
	public void write(List<? extends FeiPriceData> items) throws Exception {
		for(FeiPriceData item : items) {
			if(feiPriceDataMap.containsKey(item.getMpid())) {
				List<FeiPriceData> existingRecords = feiPriceDataMap.get(item.getMpid()).stream().filter(p -> p.getFeiPriceDataStatus() == null || p.getFeiPriceDataStatus() == FeiPriceDataStatus.VALID || p.getFeiPriceDataStatus() == FeiPriceDataStatus.OVERRIDE).collect(Collectors.toList());
				if(!existingRecords.isEmpty()) {
					//There is only ever 1 valid or override record
					FeiPriceData existingRecord = existingRecords.get(0);
					if(feiPriceDataComparator.compare(item,existingRecord) > 0) {
						feiPriceDataMap.get(item.getMpid()).remove(existingRecord);
					} else if(feiPriceDataComparator.compare(item,existingRecord) < 0) {
						continue;
					} else {
						item.setFeiPriceDataStatus(FeiPriceDataStatus.UNRESOLVED_DUPLICATE_MPID);
					}
				}

			}
			feiPriceDataMap.computeIfAbsent(item.getMpid(),p -> new ArrayList<>()).add(item);
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

				List<String> deprioritizedBrands = feiPriceService.getDeprioritizedBrandViews().stream().map(DeprioritizedBrandView::getManufacturerName).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(deprioritizedBrands)) {
					if (deprioritizedBrands.contains(o1.getBrand()) && !deprioritizedBrands.contains(o2.getBrand())) {
						return -1;
					} else if (deprioritizedBrands.contains(o2.getBrand())) {
						return 1;
					}
				}

				double o1Price = Double.parseDouble(o1.getPrice());
				double o2Price = Double.parseDouble(o2.getPrice());
				//Doesn't meet any of the criteria to pick a higher priority
				if(BigDecimal.valueOf(o1Price).compareTo(BigDecimal.valueOf(o2Price)) != 0) {
					duplicateData.add(o1);
					duplicateData.add(o2);
				}
				return 0;
			};
		}
}
