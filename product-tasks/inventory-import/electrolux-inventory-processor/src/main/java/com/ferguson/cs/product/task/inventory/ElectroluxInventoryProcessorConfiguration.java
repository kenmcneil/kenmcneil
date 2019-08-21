package com.ferguson.cs.product.task.inventory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ws.MarshallingWebServiceOutboundGateway;
import org.springframework.messaging.MessageHandler;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.destination.DestinationProvider;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryRequest;
import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
public class ElectroluxInventoryProcessorConfiguration {

	private ElectroluxInventorySettings electroluxInventorySettings;

	private static final String OUTBOUND_ELECTROLUX_CHANNEL = "outboundElectroluxChannel";
	private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String ELECTROLUX_NAMESPACE = "http://eluxna.com";

	@Autowired
	public void setElectroluxInventorySettings(ElectroluxInventorySettings electroluxInventorySettings) {
		this.electroluxInventorySettings = electroluxInventorySettings;
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
			public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
				if (SOAP_NAMESPACE.equals(namespaceUri)) {
					return "SOAP-ENV";
				} else if(ELECTROLUX_NAMESPACE.equals(namespaceUri)) {
					return "NS1";
				}
				return null;
			}
		};

		Map<String, Object> marshallerProperties = new HashMap<>();
		marshallerProperties.put("com.sun.xml.bind.namespacePrefixMapper", mapper);
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setMarshallerProperties(marshallerProperties);
		marshaller.setClassesToBeBound(ElectroluxInventoryResponse.class,ElectroluxInventoryRequest.class);
		return marshaller;
	}

	@Bean
	public DestinationProvider destinationProvider() {
		return () -> URI.create(electroluxInventorySettings.getApiUrl());
	}

	@Bean
	@ServiceActivator(inputChannel = OUTBOUND_ELECTROLUX_CHANNEL)
	public MessageHandler soapOutboundGateway(Jaxb2Marshaller marshaller, DestinationProvider destinationProvider) {
		MarshallingWebServiceOutboundGateway marshallingWebServiceOutboundGateway = new MarshallingWebServiceOutboundGateway(destinationProvider,marshaller);
		marshallingWebServiceOutboundGateway.setInterceptors(new ClientInterceptor() {
			@Override
			public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
				TransportContext context = TransportContextHolder.getTransportContext();
				HttpUrlConnection connection = (HttpUrlConnection) context
						.getConnection();
				connection.getConnection().addRequestProperty("x-api-key",
						electroluxInventorySettings.getApiKey());

				return true;
			}

			@Override
			public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
				return true;
			}

			@Override
			public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
				return true;
			}

			@Override
			public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
			}
		});

		return marshallingWebServiceOutboundGateway;
	}

	@MessagingGateway
	public interface ElectroluxGateway {
		@Gateway(requestChannel = OUTBOUND_ELECTROLUX_CHANNEL )
		ElectroluxInventoryResponse getElectroluxInventoryData(ElectroluxInventoryRequest electroluxInventoryRequest);
	}
}
