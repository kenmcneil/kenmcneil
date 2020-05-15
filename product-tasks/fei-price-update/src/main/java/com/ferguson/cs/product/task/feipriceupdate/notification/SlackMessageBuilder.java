package com.ferguson.cs.product.task.feipriceupdate.notification;

import java.util.Optional;

import org.springframework.util.StringUtils;

public class SlackMessageBuilder {

	private final NotificationSettings notificationSettings;
	private SlackMessageType messageType;
	private String channel;
	private String message;
	private Throwable exception;

	public SlackMessageBuilder(NotificationSettings notificationSettings) {
		this.notificationSettings = notificationSettings;
	}

	public SlackMessageBuilder newInstance() {
		return new SlackMessageBuilder(notificationSettings);
	}

	public SlackMessageBuilder channel(String channel) {
		this.channel = channel;
		return this;
	}

	public SlackMessageBuilder message(String message) {
		this.message = message;
		return this;
	}

	public SlackMessageBuilder throwable(Throwable exception) {
		this.exception = exception;
		return this;
	}

	public SlackMessageBuilder messageType(SlackMessageType slackMessageType) {
		this.messageType = slackMessageType;
		return this;
	}

	public SlackMessage build() {

		SlackMessage slackMessage = new SlackMessage();
		slackMessage.setText(getTitle());
		slackMessage.setChannel(channel);

		return slackMessage;

	}

	/**
	 * Title for SlackMessage
	 *
	 * @return
	 */
	private String getTitle() {
		String title = String.format("*product-services* %s", messageType);

		if (StringUtils.hasText(message)) {
			title = String.format("%s%n%s", title, message);
		}
		if (exception != null) {
			title = String.format("%s%n%s", title, getExceptionMessage(exception));
		}
		return title;
	}

	/**
	 * Returns a formatted string of 'className: message \n\t cause.className: cause.message"
	 * @param exception
	 * @return
	 */
	private Optional<String> getExceptionMessage(Throwable exception) {
		if (exception == null) {
			return Optional.empty();
		}
		String errMessage = String.format("*%s*: %s",exception.getClass().getName(), exception.getLocalizedMessage());
		if (exception.getCause() != null) {
			errMessage = errMessage.concat("\n\t").concat(exception.getCause().getClass().getName());
		}
		if (exception.getCause() != null && StringUtils.hasText(exception.getCause().getMessage())) {
			errMessage = errMessage.concat(": ").concat(exception.getCause().getMessage());
		}
		return Optional.of(errMessage);
	}

}
