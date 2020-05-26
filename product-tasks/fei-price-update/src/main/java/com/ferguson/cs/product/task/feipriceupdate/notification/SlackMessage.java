package com.ferguson.cs.product.task.feipriceupdate.notification;

/**
 * Message posted to DataFlow Notifications SlackWebhook App
 * where	text is the messagebody
 * 			channel is the slack channel to which the message is posted
 * @author patrick.way
 *
 */
public class SlackMessage {
	// Channel Name
	private String channel;
	// Message Body
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
