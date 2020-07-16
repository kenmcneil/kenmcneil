package com.ferguson.cs.product.task.feipricefeed.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ferguson.cs.product.task.feipricefeed.model.EmailRequest;

@FeignClient(name="${build-webservices.serviceId:build-webservices}")
@Component
public interface BuildWebServicesFeignClient {
	@RequestMapping(value="/emails/queueEmail", method = RequestMethod.POST)
	void queueEmail(@RequestBody EmailRequest emailRequest);

}
