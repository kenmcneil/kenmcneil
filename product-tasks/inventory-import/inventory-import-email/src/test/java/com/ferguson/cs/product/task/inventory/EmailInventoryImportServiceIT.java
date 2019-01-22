package com.ferguson.cs.product.task.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.task.inventory.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.task.inventory.InventoryImportSettings;
import com.ferguson.cs.product.task.inventory.service.InventoryImportService;
import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;
import com.ferguson.cs.test.BaseTest;


import sun.plugin2.message.Message;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTaskIntegrationTesting
@Import(InventoryImportCommonConfiguration.class)
@Transactional
public class EmailInventoryImportServiceIT extends BaseTest {


	private InventoryImportService inventoryImportService;
	private InventoryImportSettings inventoryImportSettings;

	@Autowired
	@Qualifier("emailInventoryImportService")
	public void setInventoryImportService(InventoryImportService inventoryImportService) {
		this.inventoryImportService = inventoryImportService;
	}


	@Autowired
	public void setEmailInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}



	@Test
	@Ignore("Manual run only")
	public void testImportInventory() {
		inventoryImportService.importInventory();
		File inventoryDirectory = new File(inventoryImportSettings.getInventoryDirectory());

		assertThat(inventoryDirectory.isDirectory()).isTrue();
		assertThat(inventoryDirectory.listFiles()).isNotEmpty();

	}
}
