package com.ferguson.cs.product.task.feipriceupdate.notification;

import org.springframework.beans.factory.annotation.Value;

public class NotificationSettings {

	@Value("${slack.webhook.url:}")
	private String slackWebhookUrl;

	@Value("${slack.webhook.channel:}")
	private String channel;

	@Value("${slack.webhook.enabled:}")
	private Boolean enabled;

	public String getSlackChannel() {
		return channel;
	}

	public String getSlackWebhookUrl() {
		return slackWebhookUrl;
	}

	public Boolean isSlackEnabled() {
		return enabled;
	}
}
