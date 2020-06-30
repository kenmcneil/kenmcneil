package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.batch.item.file.transform.ExtractorLineAggregator;
import org.springframework.util.ObjectUtils;

public class MpnMpidMismatchLineAggregator <T> extends ExtractorLineAggregator<T> {


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

			String text;
			if(fields[i] instanceof BigDecimal) {
				text = new DecimalFormat("0.00").format(fields[i]);
			} else {
				text = StringEscapeUtils.escapeCsv(fields[i].toString());
			}

			sb.append(text);
		}
		return sb.toString();
	}


}
