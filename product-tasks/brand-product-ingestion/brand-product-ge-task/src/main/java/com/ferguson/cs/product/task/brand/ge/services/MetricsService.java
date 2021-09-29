package com.ferguson.cs.product.task.brand.ge.services;

import java.util.Map;

public interface MetricsService {

	void addCustomParameter(String key, String value);
	void addCustomParameter(String key, Number value);
	void incrementCounter(String name);
	void noticeError(String message);
	void noticeError(String message, Map<String, ? extends Object> params);
	void noticeError(String message, boolean expected);
	void noticeError(Throwable throwable);
	void noticeError(Throwable throwable, Map<String, ? extends Object> params);
	void noticeError(Throwable throwable, boolean expected);
	void noticeError(Throwable throwable, String paramName, String paramValue);
	void recordEvent(String eventType, Map<String, Object> params);
	void setAppServerPort(int portNumber);
	void setInstanceName(String name);
	void setTransactionName(String category, String name);
	void startSegment(String segment);

}
