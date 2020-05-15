package com.ferguson.cs.product.task.feipriceupdate.notification;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationServiceImpl implements NotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private final RestTemplate restTemplate;
	private final NotificationSettings notificationSettings;
	private final SlackMessageBuilder slackMessageBuilder;

	public NotificationServiceImpl(RestTemplateBuilder restTemplateBuilder, NotificationSettings notificationSettings, SlackMessageBuilder slackMessageBuilder) {
		this.restTemplate = restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(1))
				.setReadTimeout(Duration.ofSeconds(2))
				.build();
		this.notificationSettings = notificationSettings;
		this.slackMessageBuilder = slackMessageBuilder;
	}
	@Override
	public void message(String message, SlackMessageType slackMessageType) {

		SlackMessage slackMessage = slackMessageBuilder.newInstance()
				.channel(notificationSettings.getSlackChannel())
				.message(message)
				.messageType(slackMessageType)
				.build();

		postMessage(slackMessage);
	}

	@Override
	public void message(String message, SlackMessageType slackMessageType, Throwable exception) {

		SlackMessage slackMessage = slackMessageBuilder.newInstance()
				.channel(notificationSettings.getSlackChannel())
				.message(message)
				.throwable(exception)
				.messageType(slackMessageType)
				.build();

		postMessage(slackMessage);
	}

	@Override
	public void postMessage(SlackMessage slackMessage) {
		if (notificationSettings.isSlackEnabled()) {

			ResponseEntity<String> response = restTemplate.postForEntity(notificationSettings.getSlackWebhookUrl(),
					slackMessage, String.class);

			if (!HttpStatus.OK.equals(response.getStatusCode())) {
				LOGGER.error("Error Posting DataFlow Message to Slack Webhook");
			}
		} else if (slackMessage != null) {
			LOGGER.info("Not posting slack message: {}", slackMessage.getText());
		}
	}
}
