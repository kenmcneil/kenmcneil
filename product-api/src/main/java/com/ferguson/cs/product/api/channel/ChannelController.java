package com.ferguson.cs.product.api.channel;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.product.Product;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;

@RestController
@RequestMapping("/channels")
public class ChannelController {

	private final ChannelService channelService;

	public ChannelController(ChannelService channelService) {
		this.channelService = channelService;
	}

	public Channel getChannelByCode(String code) {
		return OptionalResourceHelper.handle(channelService.getChannelByCode(code), "channel", code);
	}

	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		return channelService.getChannelsByBusinessUnit(businessUnit);
	}

	public Channel saveChannel(Channel channel) {
		return channelService.saveChannel(channel);
	}

	public void deleteChannel(String code) {
		Channel channel = getChannelByCode(code);
		channelService.deleteChannel(channel);
	}

	public List<Taxonomy> getTaxonomiesByChannel(Channel channel) {
		return channelService.getTaxonomiesByChannel(channel);
	}

	public List<Product> getFilteredProductsByChannel(Channel channel, List<String> productListId) {
		return channelService.getFilteredProductsByChannel(channel, productListId);
	}

	public List<Product> getProductsByChannel(Channel channel, Pageable pageable) {
		return channelService.getProductsByChannel(channel, pageable);
	}

	public void addProductsToChannel(Channel channel, List<String> productIdList) {
		channelService.addProductsToChannel(channel, productIdList);
	}

	public void removeProductsFromChannel(Channel channel, List<String> productIdList) {
		channelService.removeProductsFromChannel(channel, productIdList);
	}

}
