package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.newrelic.api.agent.NewRelic;

@Service
public class NewRelicMetricsServiceImpl implements MetricsService {

	@Override
	public void addCustomParameter(String key, Number value) {
		NewRelic.addCustomParameter(key, value);
	}

	@Override
	public void addCustomParameter(String key, String value) {
		NewRelic.addCustomParameter(key, value);
	}

	@Override
	public void incrementCounter(String name) {
		NewRelic.incrementCounter(name);
	}

	@Override
	public void noticeError(String message) {
		NewRelic.noticeError(message);
	}

	@Override
	public void noticeError(String message, Map<String, ? extends Object> params) {
		NewRelic.noticeError(message, params);
	}

	@Override
	public void noticeError(String message, boolean expected) {
		NewRelic.noticeError(message, expected);
	}

	@Override
	public void noticeError(Throwable throwable) {
		NewRelic.noticeError(throwable);
	}

	@Override
	public void noticeError(Throwable throwable, Map<String, ?> params) {
		NewRelic.noticeError(throwable, params);
	}

	@Override
	public void noticeError(Throwable throwable, boolean expected) {
		NewRelic.noticeError(throwable, expected);
	}

	@Override
	public void noticeError(Throwable throwable, String paramName, String paramValue) {
		NewRelic.noticeError(throwable, Collections.singletonMap(paramName, paramValue));
	}

	@Override
	public void recordEvent(String eventType, Map<String, Object> params) {
		NewRelic.getAgent().getInsights().recordCustomEvent(eventType, params);
	}

	@Override
	public void setAppServerPort(int portNumber) {
		NewRelic.setAppServerPort(portNumber);
	}

	@Override
	public void setInstanceName(String name) {
		NewRelic.setInstanceName(name);
	}

	@Override
	public void setTransactionName(String category, String name) {
		NewRelic.setTransactionName(category, name);
	}

	@Override
	public void startSegment(String segment) {
		NewRelic.getAgent().getTransaction().startSegment(segment);
	}

}
