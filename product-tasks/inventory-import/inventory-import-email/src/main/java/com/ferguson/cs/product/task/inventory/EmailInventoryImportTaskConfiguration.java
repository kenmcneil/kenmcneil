package com.ferguson.cs.product.task.inventory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;

import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.auth.publicClient.UsernamePasswordProvider;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
public class EmailInventoryImportTaskConfiguration {

	private EmailInventoryImportSettings emailInventoryImportSettings;

	@Autowired
	public void setEmailInventoryImportSettings(EmailInventoryImportSettings emailInventoryImportSettings) {
		this.emailInventoryImportSettings = emailInventoryImportSettings;
	}

	@Bean
	public IGraphServiceClient graphServiceClient() {
		List<String> scopes = new ArrayList<>();
		scopes.add("Mail.ReadWrite.Shared");

		UsernamePasswordProvider  authProvider = new UsernamePasswordProvider(emailInventoryImportSettings.getClientId(), scopes, emailInventoryImportSettings.getEmailUsername(), emailInventoryImportSettings.getEmailPassword(), NationalCloud.Global, emailInventoryImportSettings.getTenantId(), emailInventoryImportSettings.getClientSecret());

		return GraphServiceClient
				.builder()
				.authenticationProvider(authProvider)
				.buildClient();

	}
}
