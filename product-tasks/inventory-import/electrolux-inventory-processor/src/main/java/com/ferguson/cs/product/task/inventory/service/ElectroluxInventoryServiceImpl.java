package com.ferguson.cs.product.task.inventory.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.WebServiceTransportException;

import com.ferguson.cs.product.task.inventory.ElectroluxInventoryProcessorConfiguration.ElectroluxGateway;
import com.ferguson.cs.product.task.inventory.ElectroluxInventorySettings;
import com.ferguson.cs.product.task.inventory.InventoryImportSettings;
import com.ferguson.cs.product.task.inventory.dao.core.ElectroluxInventoryDao;
import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryRequest;
import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;
import com.ferguson.cs.product.task.inventory.model.ElectroluxSkuVendorData;
import com.opencsv.CSVWriter;

@Service
public class ElectroluxInventoryServiceImpl implements ElectroluxInventoryService {

	private ElectroluxInventorySettings electroluxInventorySettings;
	private InventoryImportSettings inventoryImportSettings;
	private ElectroluxGateway electroluxGateway;
	private ElectroluxInventoryDao electroluxInventoryDao;


	private static final Logger log = LoggerFactory.getLogger(ElectroluxInventoryServiceImpl.class);


	@Autowired
	public void setElectroluxInventorySettings(ElectroluxInventorySettings electroluxInventorySettings) {
		this.electroluxInventorySettings = electroluxInventorySettings;
	}

	@Autowired
	public void setInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}

	@Autowired
	public void setElectroluxGateway(ElectroluxGateway electroluxGateway) {
		this.electroluxGateway = electroluxGateway;
	}

	@Autowired
	public void setElectroluxInventoryDao(ElectroluxInventoryDao electroluxInventoryDao) {
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

				for (ElectroluxSkuVendorData electroluxSkuVendorData : electroluxSkuVendorDataList) {
					ElectroluxInventoryRequest request = new ElectroluxInventoryRequest();
					request.setCustomerId(electroluxInventorySettings.getCustomerId());
					request.setItemId(electroluxSkuVendorData.getSku());
					request.setWareHouseCode(electroluxInventorySettings.getVendorUidWarehouseMap()
							.get(electroluxSkuVendorData.getVendorUid()));
					if (request.getWareHouseCode() != null && request.getItemId() != null) {

						ElectroluxInventoryResponse response;
						try {
							response = getElectroluxInventoryData(request);
						} catch (WebServiceTransportException e) {
							response = new ElectroluxInventoryResponse();
							response.setError(e.toString());
						}

						if (response.getAvailableQuantity() != null) {
							String[] row = new String[]{electroluxSkuVendorData.getSku(), electroluxSkuVendorData
									.getVendorUid().toString(), response.getAvailableQuantity().toString()};

							writer.writeNext(row);
							break;
						}
					}
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


	@Retryable(backoff = @Backoff)
	private ElectroluxInventoryResponse getElectroluxInventoryData(ElectroluxInventoryRequest request) {
		return electroluxGateway.getElectroluxInventoryData(request);
	}

}
