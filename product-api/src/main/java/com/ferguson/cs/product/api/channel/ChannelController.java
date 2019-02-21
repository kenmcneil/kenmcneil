package com.ferguson.cs.product.api.channel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping(value = "/{code}")
	public Channel getChannelByCode(String code) {
		return OptionalResourceHelper.handle(channelService.getChannelByCode(code), "channel", code);
	}

	@PostMapping(value = "")
	public Channel saveChannel(Channel channel) {
		return channelService.saveChannel(channel);
	}

	@DeleteMapping(value = "/{code}")
	public void deleteChannel(@PathVariable("code") String code) {
		Channel channel = getChannelByCode(code);
		channelService.deleteChannel(channel);
	}

	@GetMapping(value = "/{code}/taxonomies")
	public List<Taxonomy> getTaxonomiesByChannel(@PathVariable("code") String code) {
		Channel channel = getChannelByCode(code);
		return channelService.getTaxonomiesByChannel(channel);
	}


	@PostMapping(value = "/{code}/filterProducts")
	public List<Product> getFilteredProductsByChannel(@PathVariable("code") String code, @RequestBody List<String> productListId) {
		Channel channel = getChannelByCode(code);
		return channelService.getFilteredProductsByChannel(channel, productListId);
	}

	@GetMapping(value = "/{code}/products-references")
	public List<Product> getProductsByChannel(@PathVariable("code") String code, Pageable pageRequest) {
		Channel channel = getChannelByCode(code);

		Page<Product> page = channelService.getProductsByChannel(channel, pageRequest);
		return page.getContent();
	}

	@PostMapping(value = "/{code}/product-references")
	public void addProductsToChannel(@PathVariable("code") String code, @RequestBody List<String> productIdList) {
		Channel channel = getChannelByCode(code);
		channelService.addProductsToChannel(channel, productIdList);
	}

	@PostMapping(value = "/{code}/product-references/delete")
	public void removeProductsFromChannel(@PathVariable("code") String code, @RequestBody List<String> productIdList) {
		Channel channel = getChannelByCode(code);
		channelService.removeProductsFromChannel(channel, productIdList);
	}

}
