package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Request for sending email via Spring-Email-Service
 */
public class EmailRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String to;
	private String cc;
	private String bcc;
	private String from;
	private String replyTo;
	private String subject;
	private List<String> attachments;
	private Map<String, Object> actionableData;

	private String templateName;
	private Map<String, Object> templateData;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public Map<String, Object> getActionableData() {
		return actionableData;
	}

	public void setActionableData(Map<String, Object> actionableData) {
		this.actionableData = actionableData;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, Object> getTemplateData() {
		return templateData;
	}

	public void setTemplateData(Map<String, Object> templateData) {
		this.templateData = templateData;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}