package com.ferguson.cs.product.task.inventory.service;

import java.util.List;

import javax.mail.Message;

public interface EmailService {
	List<Message> retrieveEmailMessages() throws javax.mail.MessagingException;
}
