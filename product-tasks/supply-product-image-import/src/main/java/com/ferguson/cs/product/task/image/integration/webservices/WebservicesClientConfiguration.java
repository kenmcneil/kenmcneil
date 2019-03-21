package com.ferguson.cs.product.task.image.integration.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.client.RestTemplate;

import com.ferguson.cs.product.task.image.integration.ClientConfiguration;
import com.ferguson.cs.product.task.image.integration.DefaultClientErrorHandler;



@ConfigurationProperties(prefix = "webservices-client")
@Configuration()
public class WebservicesClientConfiguration implements ClientConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebservicesClientConfiguration.class);

	private String remoteServerAddress;

	@Qualifier("webservices-rest-template")
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String getRemoteServerAddress() {
		return remoteServerAddress;
	}

	public void setRemoteServerAddress(String remoteServerAddress) {
		this.remoteServerAddress = remoteServerAddress;
	}

	@Override
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@Bean(name = "webservices-rest-template")
	@ConditionalOnProperty(prefix = "eureka.client", name = "enabled", havingValue = "false")
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Primary
	public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		LOGGER.info("Initializing ws rest-template > not load-balanced.");
		return createRestTemplate(restTemplateBuilder);
	}

	// We conditionally choose to use the load balanced or regular rest template
	// based on the "eureka.client.enabled" flag.
	@Bean(name = "webservices-rest-template")
	@ConditionalOnProperty(prefix = "eureka.client", name = "enabled", matchIfMissing = true)
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@LoadBalanced
	@Primary
	public RestTemplate getLoadBalancedRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		LOGGER.info("Initializing ws rest-template > load-balanced.");
		return createRestTemplate(restTemplateBuilder);
	}

	private RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		// Note: The connection/read timeout for this rest templates are defined via a
		// custom RestTemplateBuilder
		// from the Build's cloud-utils library.
		LOGGER.info("Initializing ws rest-template > setting error handler.");
		return restTemplateBuilder.errorHandler(new DefaultClientErrorHandler()).build();
	}

}
