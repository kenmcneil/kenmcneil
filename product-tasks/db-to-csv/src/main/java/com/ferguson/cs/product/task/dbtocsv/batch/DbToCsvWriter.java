package com.ferguson.cs.product.task.dbtocsv.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class DbToCsvWriter implements ItemWriter<String> {

	@Override
	public void write(List<? extends String> items) throws Exception {

		items.forEach(item -> System.out.println(item));

	}

}