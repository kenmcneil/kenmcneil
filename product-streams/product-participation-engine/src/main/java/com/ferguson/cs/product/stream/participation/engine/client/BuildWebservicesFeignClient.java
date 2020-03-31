package com.ferguson.cs.product.stream.participation.engine.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ferguson.cs.product.stream.participation.engine.model.EmailRequest;

@FeignClient(name="${build-webservices.serviceId:build-webservices}")
public interface BuildWebservicesFeignClient {
	@PostMapping(value = "/emails/queueEmail")
	void queueEmail(@RequestBody EmailRequest request);
}
