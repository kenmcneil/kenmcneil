package com.ferguson.cs.product.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ferguson.cs.task.SimpleTaskRunner;

@Component
public class FtpInventoryImportTaskRunner implements SimpleTaskRunner{
	private FtpInventoryImportTask ftpInventoryImportTask;

	@Autowired
	public void setOrderlyCreateRiskPoTask(FtpInventoryImportTask ftpInventoryImportTask) {
		this.ftpInventoryImportTask = ftpInventoryImportTask;
	}

	@Override
	public void runTask() throws Exception {
		ftpInventoryImportTask.importInventoryViaFtp();
	}
}
