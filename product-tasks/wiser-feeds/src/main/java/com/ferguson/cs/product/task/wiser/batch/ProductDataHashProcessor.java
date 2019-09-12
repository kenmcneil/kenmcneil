package com.ferguson.cs.product.task.wiser.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import com.ferguson.cs.product.task.wiser.model.ConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserSale;
import com.ferguson.cs.product.task.wiser.service.WiserService;


public class ProductDataHashProcessor implements ItemProcessor<ProductData,ProductDataHash>,StepExecutionListener {

	private WiserService wiserService;
	private Map<Integer,WiserSale> wiserSaleMap;
	private Map<Integer,ProductDataHash> previousHashMap;
	private Map<Integer, ProductRevenueCategory> productRevenueCategorization;
	private Map<Integer, ProductConversionBucket> productConversionBuckets;
	private Date date;
	private static final String HASHING_METHOD = "MD5";



	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Autowired
	public void setProductRevenueCategorization(Map<Integer,ProductRevenueCategory> productRevenueCategorization) {
		this.productRevenueCategorization = productRevenueCategorization;
	}

	@Autowired
	public void setProductConversionBuckets(Map<Integer, ProductConversionBucket> productConversionBuckets) {
		this.productConversionBuckets = productConversionBuckets;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
		wiserSaleMap = new HashMap<>();
		date = wiserService.getLastRanDate(jobName);
		List<WiserSale> wiserSaleList = wiserService.getWiserSales(date);

		List<ProductDataHash> previousHashList = wiserService.getAllProductDataHashes();
		for (Iterator<WiserSale> it = wiserSaleList.iterator(); it.hasNext(); ) {
			WiserSale e = it.next();
			it.remove();
			wiserSaleMap.put(e.getProductUniqueId(),e);
		}

		previousHashMap = new HashMap<>();
		for(ProductDataHash hash : previousHashList) {
			previousHashMap.put(hash.getProductUniqueId(),hash);
		}

		for (Iterator<ProductDataHash> it = previousHashList.iterator(); it.hasNext(); ) {
			ProductDataHash e = it.next();
			it.remove();
			previousHashMap.put(e.getProductUniqueId(),e);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

	@Override
	public ProductDataHash process(ProductData productData) throws Exception {
		if(!isValid(productData)) {
			return null;
		}


		WiserSale sale = wiserSaleMap.get(productData.getUniqueId());
		boolean isItemPromo = wiserService.isItemPromo(sale,date);
		productData.setPromo(isItemPromo);
		ProductRevenueCategory productRevenueCategory = productRevenueCategorization.get(productData.getUniqueId());
		if(productRevenueCategory != null) {
			productData.setHctCategory(productRevenueCategory.getRevenueCategory()
					.getStringValue());
		}
		ProductConversionBucket productConversionBucket = productConversionBuckets.get(productData.getUniqueId());
		if(productConversionBucket != null) {
			productData.setConversionCategory(productConversionBucket.getConversionBucket().getStringValue());
		} else {
			productData.setConversionCategory(ConversionBucket.MEDIUM.getStringValue());
		}


		ProductDataHash productDataHash = new ProductDataHash();
		productDataHash.setHashCode(hash(productData));
		productDataHash.setProductUniqueId(productData.getUniqueId());
		productDataHash.setLastModifiedDate(new Date());


		ProductDataHash previousHash = previousHashMap.get(productDataHash.getProductUniqueId());

		if(previousHash != null && productDataHash.getHashCode().equalsIgnoreCase(previousHash.getHashCode())) {
			return null;
		}

		return productDataHash;
	}


	private String hash(ProductData productData) throws IOException,NoSuchAlgorithmException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(productData);
		oos.close();

		MessageDigest md = MessageDigest.getInstance(HASHING_METHOD);
		md.update(baos.toByteArray());

		return DigestUtils.md5DigestAsHex(md.digest()).toUpperCase();

	}

	private boolean isValid(ProductData productData) {
		return (productData != null
				&& productData.getUniqueId() != null
				&& productData.getProductId() != null
				&& productData.getManufacturer() != null
				&& productData.getCompositeId() != null
				&& productData.getFinish() != null);
	}
}
