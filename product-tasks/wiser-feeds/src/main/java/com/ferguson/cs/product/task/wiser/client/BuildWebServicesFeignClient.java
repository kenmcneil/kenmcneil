package com.ferguson.cs.product.task.wiser.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ferguson.cs.product.task.wiser.model.EmailRequest;
import com.ferguson.cs.product.task.wiser.model.FileType;

@FeignClient(name="${build-webservices.serviceId:build-webservices}")
@Component
public interface BuildWebServicesFeignClient {
	@RequestMapping(value="/emails/queueEmail", method = RequestMethod.POST)
	void queueEmail(@RequestBody EmailRequest emailRequest);

	@RequestMapping(value = "/file/createCostUploadJob", method = RequestMethod.POST)
	void createCostUploadJob(@RequestParam("filePath") String filePath,
							 @RequestParam("fileType") FileType fileType,
							 @RequestParam("processDate") String processDate,
							 @RequestParam("userId") Integer userId);
}
