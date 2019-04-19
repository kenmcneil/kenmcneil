package com.ferguson.cs.product.task.supply;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CsvHeaderWriterCallback implements FlatFileHeaderCallback {

	private final String[] headerColumns;
	private final String delimiter;

	public CsvHeaderWriterCallback(String[] headerColumns, String delimiter) {
		this.headerColumns = headerColumns;
		this.delimiter = delimiter;
	}
	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write(String.join(delimiter,headerColumns));
	}
}
