package com.ferguson.cs.product.task.dy.batch;

import java.text.DecimalFormat;

import org.springframework.batch.item.file.transform.ExtractorLineAggregator;
import org.springframework.util.ObjectUtils;

public class QuoteEnclosingDelimitedLineAggregator<T> extends ExtractorLineAggregator<T> {

	private String delimiter;

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public String doAggregate(Object[] fields) {

		if (ObjectUtils.isEmpty(fields)) {
			return "";
		}
		if (fields.length == 1) {
			return "\"" + ObjectUtils.nullSafeToString(fields[0]) + "\"";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				sb.append(delimiter);
			}
			sb.append("\"");
			String text;
			if(fields[i] instanceof Double) {
				text = new DecimalFormat("0.00").format(fields[i]);
			} else {
				text = fields[i].toString();
				text = text.replace("\\", "").replace("\"","").replace(",","");
			}

			sb.append(text);
			sb.append("\"");
		}
		return sb.toString();
	}


}
