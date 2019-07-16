package com.ferguson.cs.product.task.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.stereotype.Component;

@Component
public class InventoryEmailServiceImpl implements EmailService {

	private MailReceiver inventoryMailReceiver;

	@Autowired
	public void setInventoryMailReceiver(MailReceiver inventoryMailReceiver) {
		this.inventoryMailReceiver = inventoryMailReceiver;
	}

	@Override
	public List<javax.mail.Message> retrieveEmailMessages() throws javax.mail.MessagingException {

		Object[] rawMessages = inventoryMailReceiver.receive();

		List<javax.mail.Message> messages = new ArrayList<>();
		for (Object rawMessage : rawMessages) {
			messages.add((javax.mail.Message)rawMessage);
		}
		return messages;
	}

}
