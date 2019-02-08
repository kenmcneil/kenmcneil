package com.ferguson.cs.product.task.supply;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.batch.item.ItemWriter;

public class XlsxItemWriter<T> implements ItemWriter<T> {




	@Override
	public void write(List<? extends T> list) throws Exception {
		//
	}
}
