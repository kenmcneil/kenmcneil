package com.ferguson.cs.product.task.dbtocsv.batch;

import org.springframework.batch.item.ItemProcessor;


public class DbToCsvProcessor implements ItemProcessor<String, String> {

	@Override
	public String process(String item) throws Exception {

		return item + "-Processed";
	}

}
