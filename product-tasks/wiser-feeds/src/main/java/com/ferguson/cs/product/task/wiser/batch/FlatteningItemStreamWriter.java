package com.ferguson.cs.product.task.wiser.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class FlatteningItemStreamWriter<T> implements ItemStreamWriter<List<T>>{
	ItemStreamWriter<T> itemStreamWriter;

	public FlatteningItemStreamWriter(ItemStreamWriter<T> itemStreamWriter) {
		this.itemStreamWriter = itemStreamWriter;
	}

	@Override
	public void write(List<? extends List<T>> items) throws Exception {
		List<T> flattenedList = new ArrayList<>();

		for(List<T> item : items) {
			flattenedList.addAll(item);
		}
		itemStreamWriter.write(flattenedList);
	}


	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		itemStreamWriter.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		itemStreamWriter.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		itemStreamWriter.close();
	}
}
