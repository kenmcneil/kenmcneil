package com.ferguson.cs.product.task.wiser.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.WiserSale;
import com.ferguson.cs.product.task.wiser.service.WiserService;


public class ProductDataHashProcessor implements ItemProcessor<ProductData,ProductDataHash>,StepExecutionListener {

	private WiserService wiserService;
	private Map<Integer,WiserSale> wiserSaleMap;
	private Map<Integer,ProductDataHash> previousHashMap;
	private Date date;
	private static final String HASHING_METHOD = "MD5";



	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
		wiserSaleMap = new HashMap<>();
		date = wiserService.getLastRanDate(jobName);
		List<WiserSale> wiserSaleList = wiserService.getWiserSales(date);

		List<ProductDataHash> previousHashList = wiserService.getAllProductDataHashes();
		for(WiserSale sale : wiserSaleList) {
			wiserSaleMap.put(sale.getProductUniqueId(),sale);
		}

		previousHashMap = new HashMap<>();
		for(ProductDataHash hash : previousHashList) {
			previousHashMap.put(hash.getProductUniqueId(),hash);
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
