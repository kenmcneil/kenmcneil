package com.ferguson.cs.product.task.inventory.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.inventory.ElectroluxInventorySettings;
import com.ferguson.cs.product.task.inventory.InventoryImportSettings;
import com.ferguson.cs.product.task.inventory.client.ElectroluxClient;
import com.ferguson.cs.product.task.inventory.dao.core.ElectroluxInventoryDao;
import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;
import com.ferguson.cs.product.task.inventory.model.ElectroluxSkuVendorData;
import com.opencsv.CSVWriter;

@Service
public class ElectroluxInventoryServiceImpl implements ElectroluxInventoryService {

	private final ElectroluxInventorySettings electroluxInventorySettings;
	private final InventoryImportSettings inventoryImportSettings;
	private final ElectroluxClient electroluxClient;
	private final ElectroluxInventoryDao electroluxInventoryDao;


	private static final Logger log = LoggerFactory.getLogger(ElectroluxInventoryServiceImpl.class);

	public ElectroluxInventoryServiceImpl(ElectroluxInventorySettings electroluxInventorySettings, InventoryImportSettings inventoryImportSettings, ElectroluxClient electroluxClient, ElectroluxInventoryDao electroluxInventoryDao) {
		this.electroluxInventorySettings = electroluxInventorySettings;
		this.inventoryImportSettings = inventoryImportSettings;
		this.electroluxClient = electroluxClient;
		this.electroluxInventoryDao = electroluxInventoryDao;
	}

	@Override
	public void writeElectroluxInventoryData() {
		Map<Integer,String> warehouseMap = electroluxInventorySettings.getVendorUidWarehouseMap();

		for(Map.Entry<Integer,String> warehouse : warehouseMap.entrySet()) {
			List<ElectroluxSkuVendorData> electroluxSkuVendorDataList = electroluxInventoryDao.getElectroluxSkus(warehouse.getKey());
			File outputFile = new File(inventoryImportSettings
					.getInventoryDirectory() + "/electrolux/" + electroluxInventorySettings.getFileNamePrefix() + warehouse.getValue() + ".csv");
			outputFile.getParentFile().mkdirs();
			File inventoryDirectory = new File(inventoryImportSettings.getInventoryDirectory());
			try (
					FileOutputStream fos = new FileOutputStream(outputFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bos, StandardCharsets.UTF_8
							.newEncoder());
					CSVWriter writer = new CSVWriter(outputStreamWriter, ',')
			) {
				String[] headerRow = new String[]{"sku", "vendorUid", "quantity"};
				writer.writeNext(headerRow);

				ElectroluxInventoryResponse response = null;
				try {
					response = electroluxClient
							.getElectroluxInventoryData(warehouse.getValue(),  electroluxSkuVendorDataList.stream().map(ElectroluxSkuVendorData::getSku).collect(Collectors.toList()));
				} catch (Exception e) {
					log.error("Failed to get Electrolux stock for vendor {}. Cause: {}", warehouse.getKey(),e.toString());
				}
				if(response != null && !CollectionUtils.isEmpty(response.getInventoryResponse())) {
					response.getInventoryResponse().forEach(p -> writer.writeNext(new String[]{p.getModelNumber(),warehouse.getKey().toString(),p.getNetInventory().toString()}));
				}
			} catch (IOException e) {
				log.error("Failed to write Electrolux inventory file: {}",e.toString());
			}

			try {
				FileUtils.moveFileToDirectory(outputFile,inventoryDirectory,false);
			} catch (IOException e) {
				log.error("Failed to move Electrolux inventory file {} to inventory directory {}",outputFile.getName(),e);
			}
		}

	}
}
