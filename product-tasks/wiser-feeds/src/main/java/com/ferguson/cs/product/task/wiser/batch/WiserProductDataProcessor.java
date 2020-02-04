package com.ferguson.cs.product.task.wiser.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.WiserProductData;
import com.ferguson.cs.product.task.wiser.model.WiserSale;
import com.ferguson.cs.product.task.wiser.service.WiserService;
import com.ferguson.cs.product.task.wiser.utility.CloudinaryHelper;

public class WiserProductDataProcessor implements ItemProcessor<ProductData, WiserProductData>, StepExecutionListener {

	private WiserService wiserService;
	private Map<Integer, WiserSale> wiserSaleMap;
	private Set<Integer> productUniqueIds;
	private Date date;

	@Autowired
	public void setProductUniqueIds(Set<Integer> productUniqueIds) {
		this.productUniqueIds = productUniqueIds;
	}


	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
		date = wiserService.getLastRanDate(jobName);
		List<WiserSale> wiserSaleList = wiserService.getWiserSales(date);
		wiserSaleMap = new HashMap<>();

		/*
		 * Memory optimization. This list can get quite large, containing both list and map in memory can be a lot,
		 * so remove items from list as they are added to map.
		 */
		for (Iterator<WiserSale> it = wiserSaleList.iterator(); it.hasNext(); ) {
			WiserSale e = it.next();
			it.remove();
			WiserSale duplicateSale = wiserSaleMap.get(e.getProductUniqueId());
			//Put into sale map if key doesn't exist or if existing key isn't promo
			if(duplicateSale == null || !wiserService.isItemPromo(duplicateSale,date)) {
				wiserSaleMap.put(e.getProductUniqueId(), e);
			}
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

	@Override
	public WiserProductData process(ProductData item) throws Exception {

		if (!isValidAndIncluded(item)) {
			return null;
		}

		if (wiserSaleMap.containsKey(item.getUniqueId())) {
			item.setPromo(wiserService.isItemPromo(wiserSaleMap.get(item.getUniqueId()), date));
		} else {
			item.setPromo(false);
		}

		WiserProductData wiserProductData = new WiserProductData();

		if (item.getManufacturer() != null && item.getProductId() != null && item
				.getCompositeId() != null && item.getUniqueId() != null) {
			String productUrl = ("www.build.com/" + item.getManufacturer() + '-' + item.getProductId() + "/s" + item
					.getCompositeId() + "?uid=" + item.getUniqueId()).replace(" ", "-");
			wiserProductData.setProductUrl(productUrl);
		}

		if (item.getManufacturer() != null && item.getImage() != null) {
			wiserProductData
					.setImageUrl(CloudinaryHelper.createCloudinaryProductUrl(item.getManufacturer(), item.getImage()));
		}

		wiserProductData.setSku(item.getUniqueId());
		wiserProductData.setProductName(item.getProductTitle() == null ? null : item.getProductTitle()
				.replaceAll("\\R", " "));
		wiserProductData.setBrand(item.getManufacturer());
		wiserProductData.setUpc(item.getUpc());
		wiserProductData.setMpnModelNumber(item.getMpn());
		wiserProductData.setL1Category(item.getBaseCategory());
		wiserProductData.setL2Category(item.getBusinessCategoryName());
		wiserProductData.setProductType(item.getType());
		wiserProductData.setOnMap(item.getMap());
		wiserProductData.setOnPromo(item.getPromo());
		wiserProductData.setInStock(item.getInStock());
		wiserProductData.setMapPrice(item.getMapPrice());
		wiserProductData.setProductPrice(item.getPrice());
		wiserProductData.setCost(item.getCost());
		wiserProductData.setApplication(item.getApplication());
		wiserProductData.setIsLtl(item.getLtl());
		wiserProductData.setSaleId(item.getSaleId());
		wiserProductData.setDateAdded(item.getDateAdded());
		wiserProductData.setListPrice(item.getListPrice());
		wiserProductData.setHctCategory(item.getHctCategory());
		wiserProductData.setConversionCategory(item.getConversionCategory());


		return wiserProductData;
	}

	private boolean isValidAndIncluded(ProductData productData) {
		return (productData != null
				&& productData.getUniqueId() != null
				&& productData.getProductId() != null
				&& productData.getManufacturer() != null
				&& productData.getCompositeId() != null
				&& productData.getFinish() != null
				&& (productUniqueIds.isEmpty() || productUniqueIds.contains(productData.getUniqueId())));
	}
}
