package com.ferguson.cs.product.task.mpnmpidmismatch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ferguson.cs.product.task.mpnmpidmismatch.util.EmailRequest;

@FeignClient(name="${build-webservices.serviceId:build-webservices}")

@Component
public interface BuildWebServicesFeignClient {
	@PostMapping("/emails/queueEmail")
	void queueEmail(@RequestBody EmailRequest emailRequest);
}
