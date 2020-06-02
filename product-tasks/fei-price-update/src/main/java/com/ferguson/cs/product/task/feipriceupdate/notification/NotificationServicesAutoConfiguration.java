package com.ferguson.cs.product.task.feipriceupdate.notification;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationServicesAutoConfiguration {

	@Bean
	public NotificationService notificationService(RestTemplateBuilder restTemplateBuilder,
			NotificationSettings notificationSettings,
			SlackMessageBuilder slackMessageBuilder) {
		return new NotificationServiceImpl(restTemplateBuilder, notificationSettings, slackMessageBuilder);
	}

	@Bean
	public NotificationSettings notificationSettings() {
		return new NotificationSettings();
	}

	@Bean
	public SlackMessageBuilder slackMessageBuilder(NotificationSettings notificationSettings) {
		return new SlackMessageBuilder(notificationSettings);
	}
}
