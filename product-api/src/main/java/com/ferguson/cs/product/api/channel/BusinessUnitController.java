package com.ferguson.cs.product.api.channel;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;

@RestController
@RequestMapping("/business-units")
public class BusinessUnitController {

	private final ChannelService channelService;

	public BusinessUnitController(ChannelService channelService) {
		this.channelService = channelService;
	}

	@GetMapping(value = "/")
	public List<BusinessUnit> getAllBusinessUnits() {
		return Arrays.asList(BusinessUnit.values());
	}

	@GetMapping(value = "/{id}/channels")
	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		return channelService.getChannelsByBusinessUnit(businessUnit);
	}
}
