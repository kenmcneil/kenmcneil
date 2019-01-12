package com.ferguson.cs.product.task;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;
import com.ferguson.cs.test.BaseTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTaskIntegrationTesting
public class FtpInventoryImportTaskIT extends BaseTest{

	private FtpInventoryImportTask ftpInventoryImportTask;

	@Autowired
	public void setFtpInventoryImportTask(FtpInventoryImportTask ftpInventoryImportTask) {
		this.ftpInventoryImportTask = ftpInventoryImportTask;
	}

	@Test
	@Ignore("Manual run only")
	public void testImportInventoryViaFtp() {
		ftpInventoryImportTask.importInventoryViaFtp();
	}
}
