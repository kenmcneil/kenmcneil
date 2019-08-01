package com.ferguson.cs.product.task.wiser.batch;

import java.util.List;
import java.util.Set;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class SetItemWriter<T> implements ItemWriter<T> {

	private Set<T> writtenItems;

	@Autowired
	public void setWrittenItems(Set<T> writtenItems) {
		this.writtenItems = writtenItems;
	}


	@Override
	public void write(List<? extends T> items) throws Exception {
		writtenItems.addAll(items);
	}
}
