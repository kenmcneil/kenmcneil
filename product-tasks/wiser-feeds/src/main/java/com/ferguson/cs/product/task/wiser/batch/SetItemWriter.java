package com.ferguson.cs.product.task.wiser.batch;

import java.util.List;
import java.util.Set;

import org.springframework.batch.item.ItemWriter;

public class SetItemWriter<T> implements ItemWriter<T> {

	private final Set<T> writtenItems;

	public SetItemWriter(Set<T> writtenItems) {
		this.writtenItems = writtenItems;
	}


	@Override
	public void write(List<? extends T> items) throws Exception {
		writtenItems.addAll(items);
	}
}
