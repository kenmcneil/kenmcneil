package com.ferguson.cs.product.task.wiser.batch;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.batch.item.file.transform.FieldExtractor;

import com.ferguson.cs.utilities.DateUtils;

public abstract class WiserFieldExtractor<T> implements FieldExtractor<T>{

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

	/**
	 * Formats objects into quote enclosed strings for Wiser. Has special logic for some boxed types.
	 * Escapes certain characters
	 *
	 * @param o - object to be formatted
	 * @return - quote enclosed string version of object
	 */
	protected static String format(Object o) {
		String text = "";

		if(o != null) {
			if(o instanceof Double) {
				text = new DecimalFormat("0.00").format(o);
			} else if(o instanceof Boolean) {
				text = (Boolean)o ? "1" : "0";
			} else if(o instanceof Date) {
				DateTimeFormatter formatter = DateUtils.getDateTimeFormatter(DATE_TIME_FORMAT);
				text = DateUtils.dateToString((Date)o,formatter);
			} else {
				text = o.toString();
				if(o instanceof String || o instanceof Character) {
					text = text.replace("\\", "\\\\").replace("\"", "\\\"");
				}
			}
		}
		return '"' + text + '"';
	}
}
