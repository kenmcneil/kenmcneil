package com.ferguson.cs.product.task.mpnmpidmismatch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductSearchCriteria;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductSearchResultView;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductView;

@FeignClient(name="${pdm.serviceId:pdm}")

public interface PdmMdmWebServicesFeignClient {
	@GetMapping(value = "/api/v1/mdm/products/{id}")
	MdmProductView getMdmProductView(@PathVariable(value = "id") Long id);

	@PostMapping(value = "/api/v1/mdm/products/search")
	MdmProductSearchResultView searchMdmProducts(@RequestBody MdmProductSearchCriteria criteria);
}
