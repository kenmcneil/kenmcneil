package com.ferguson.cs.product.task.feipriceupdate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder that is used to create an instance of EmailRequest
 */
public class EmailRequestBuilder {

	private Map<String, Object> templateData = new HashMap<>();
	private Map<String, Object> actionableData = new HashMap<>();
	private List<String> attachments = new ArrayList<>();
	private Map<String, byte[]> rawAttachments = new HashMap<>();

	private String to;
	private String cc;
	private String bcc;
	private String from;
	private String replyTo;
	private String subject;
	private Boolean bodyOnly;
	private String cssSelector;
	private String templateName;

	public static EmailRequestBuilder sendTo(String to) {
		return new EmailRequestBuilder().to(to);
	}

	public static EmailRequestBuilder sendTo(List<String> to) {
		return new EmailRequestBuilder().to(to);
	}

	public EmailRequestBuilder to(String to) {
		this.to = to;
		return this;
	}

	public EmailRequestBuilder to(List<String> to) {
		if (to != null && !to.isEmpty()) {
			this.to = String.join(",", to);
		}
		return this;
	}

	public EmailRequestBuilder cc(String cc) {
		this.cc = cc;
		return this;
	}

	public EmailRequestBuilder cc(List<String> cc) {
		if (cc != null && !cc.isEmpty()) {
			this.cc = String.join(",", cc);
		}
		return this;
	}

	public EmailRequestBuilder bcc(String bcc) {
		this.bcc = bcc;
		return this;
	}

	public EmailRequestBuilder bcc(List<String> bcc) {
		if (bcc != null && !bcc.isEmpty()) {
			this.bcc = String.join(",", bcc);
		}
		return this;
	}

	public EmailRequestBuilder from(String from) {
		this.from = from;
		return this;
	}

	public EmailRequestBuilder replyTo(String replyTo) {
		this.replyTo = replyTo;
		return this;
	}

	public EmailRequestBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	public EmailRequestBuilder bodyOnly(Boolean bodyOnly) {
		this.bodyOnly = bodyOnly;
		return this;
	}

	public EmailRequestBuilder cssSelector(String cssSelector) {
		this.cssSelector = cssSelector;
		return this;
	}

	public EmailRequestBuilder templateName(String templateName) {
		this.templateName = templateName;
		return this;
	}

	public EmailRequestBuilder addTemplateData(String key, Object value) {
		this.templateData.put(key, value);
		return this;
	}

	public EmailRequestBuilder addActionableData(String key, Object value) {
		this.actionableData.put(key, value);
		return this;
	}

	public EmailRequestBuilder addAttachment(String attachment) {
		this.attachments.add(attachment);
		return this;
	}

	public EmailRequestBuilder addRawAttachment(String fileName, byte[] attachment) {
		this.rawAttachments.put(fileName, attachment);
		return this;
	}

	public EmailRequest build() {
		EmailRequest request = new EmailRequest();

		request.setTo(this.to);
		request.setCc(this.cc);
		request.setBcc(this.bcc);
		request.setFrom(this.from);
		request.setReplyTo(this.replyTo);
		request.setSubject(this.subject);
		request.setBodyOnly(this.bodyOnly);
		request.setCssSelector(this.cssSelector);

		request.setTemplateName(this.templateName);
		request.setTemplateData(this.templateData);
		request.setAttachments(this.attachments);
		request.setRawAttachments(this.rawAttachments);
		request.setActionableData(this.actionableData);

		return request;
	}
}
