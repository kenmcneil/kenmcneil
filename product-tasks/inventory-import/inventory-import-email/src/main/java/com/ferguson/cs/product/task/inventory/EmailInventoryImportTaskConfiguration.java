package com.ferguson.cs.product.task.inventory;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
public class EmailInventoryImportTaskConfiguration {

	private EmailInventoryImportSettings emailInventoryImportSettings;

	@Autowired
	public void setEmailInventoryImportSettings(EmailInventoryImportSettings emailInventoryImportSettings) {
		this.emailInventoryImportSettings = emailInventoryImportSettings;
	}

	@Bean
	public MailReceiver inventoryMailReceiver() {

		Properties mailProperties = new Properties();

		mailProperties.setProperty("mail.store.protocol", "imaps");
		mailProperties.setProperty("mail.imap.partialfetch", "false");
		mailProperties.setProperty("mail.mime.decodetext.strict", "false");

		// SSL setting
		mailProperties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		mailProperties.setProperty("mail.imap.socketFactory.fallback", "false");
		mailProperties.setProperty("mail.imap.socketFactory.port", emailInventoryImportSettings.getEmailPort().toString());

		mailProperties.setProperty("mail.debug", "true");

		Authenticator javaMailAuthenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailInventoryImportSettings.getEmailUsername(),emailInventoryImportSettings.getEmailPassword());
			}
		};
		String url = String
				.format("imaps://%s:%s@%s:%d/INBOX", emailInventoryImportSettings.getEmailUsername(), emailInventoryImportSettings
						.getEmailPassword(), emailInventoryImportSettings.getEmailHostName(), emailInventoryImportSettings
						.getEmailPort());
		ImapMailReceiver imapMailReceiver = new ImapMailReceiver(url);
		// If safe mode is enabled, don't mark emails as read or delete them
		imapMailReceiver.setShouldMarkMessagesAsRead(!emailInventoryImportSettings.getSafeMode());
		imapMailReceiver.setShouldDeleteMessages(!emailInventoryImportSettings.getSafeMode());
		imapMailReceiver.setJavaMailProperties(mailProperties);
		imapMailReceiver.setJavaMailAuthenticator(javaMailAuthenticator);
		imapMailReceiver.setMaxFetchSize(10000);
		imapMailReceiver.setSimpleContent(true);
		imapMailReceiver.afterPropertiesSet();
		return imapMailReceiver;

	}

}
