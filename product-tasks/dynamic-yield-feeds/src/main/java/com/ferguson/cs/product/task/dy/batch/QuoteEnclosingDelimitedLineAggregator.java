package com.ferguson.cs.product.task.dy.batch;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.batch.item.file.transform.ExtractorLineAggregator;
import org.springframework.util.ObjectUtils;

import com.ferguson.cs.product.task.dy.domain.CommonConfig;
import com.ferguson.cs.product.task.dy.domain.Site;

public class QuoteEnclosingDelimitedLineAggregator<T> extends ExtractorLineAggregator<T> {

	private static final String URL_STRING_PRE = "https://www.";
	private static final String URL_STRING_POST = ".com/product/s";

	private static final String URL_UID_STRING = "?uid=";

	private String delimiter;
	private Site site;

	public QuoteEnclosingDelimitedLineAggregator(Site site) {
		this.site = site;
	}

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
			if(fields[i] instanceof BigDecimal) {
				text = new DecimalFormat("0.00").format(fields[i]);
			} else {
				text = fields[i].toString();
				text = text.replace("\\", "").replace("\"","\"\"");
				if (text.startsWith(CommonConfig.PRODUCT_URL_REPLACEMENT)) {
					String groupId;
					String sku;

					String[] urlValues = text.split(":");
					sku = urlValues[1];
					groupId = urlValues[2];

					text = URL_STRING_PRE + site.toString().toLowerCase()
							+ URL_STRING_POST + groupId + URL_UID_STRING + sku;
				}
			}

			sb.append(text);
			sb.append("\"");
		}
		return sb.toString();
	}


}
