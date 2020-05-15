package com.ferguson.cs.product.task.feipriceupdate.notification;

public interface NotificationService {

	/**
	 * Post a TaskNotificationMessage
	 *
	 * @param slackDataflowMessage
	 */
	void postMessage(SlackMessage build);

	/**
	 * Simple way to post a message
	 * @param message The string message you want to send
	 * @param slackMessageType The message severity level
	 */
	void message(String message, SlackMessageType slackMessageType);

	/**
	 * Simple way to post a message with an exception
	 * @param message The string message you want to send
	 * @param slackMessageType The message severity level
	 * @param exception The related exception
	 */
	void message(String message, SlackMessageType slackMessageType, Throwable exception);


}
