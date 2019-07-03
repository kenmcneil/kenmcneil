package com.ferguson.cs.product.task.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ferguson.cs.task.SimpleTaskRunner;

@Component
public class EmailInventoryImportTaskRunner implements SimpleTaskRunner {
	private EmailInventoryImportTask emailInventoryImportTask;

	@Autowired
	public void setOrderlyCreateRiskPoTask(EmailInventoryImportTask emailInventoryImportTask) {
		this.emailInventoryImportTask = emailInventoryImportTask;
	}

	@Override
	public void runTask() throws Exception {
		emailInventoryImportTask.importInventoryViaEmail();
	}
}
