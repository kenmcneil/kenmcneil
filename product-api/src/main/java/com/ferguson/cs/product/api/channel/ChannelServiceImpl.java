package com.ferguson.cs.product.api.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.product.Product;
import com.ferguson.cs.product.api.taxonomy.TaxonomyService;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class ChannelServiceImpl implements ChannelService {

	private final TaxonomyService taxonomyService;


	public ChannelServiceImpl(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	@Override
	public Optional<Channel> getChannelByCode(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		//return channelRepository.findByCode(code);
		return null;
	}

	@Override
	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		ArgumentAssert.notNull(businessUnit, "business unit");
		//return channelRepository.findByBusinessUnit(businessUnit);
		return null;
	}

	@Override
	public Channel saveChannel(Channel channel) {
		ArgumentAssert.notNull(channel, "channel");
		ArgumentAssert.notNullOrEmpty(channel.getCode(), "code");
		ArgumentAssert.notNull(channel.getBusinessUnit(), "business unit");

		if (channel.getIsActive() == null) {
			channel.setIsActive(Boolean.FALSE);
		}
		return null;
		//return channelRepository.save(channel);
	}

	@Override
	public void deleteChannel(final Channel channel) {
		ArgumentAssert.notNull(channel, "channel");
		ArgumentAssert.notNullOrEmpty(channel.getId(), "ID");
		//channelRepository.delete(channel);
		//TODO Need to delete all the channel/product links. Note: Mongo does not support transactions across document operations, so this is NOT atomic.
	}


	@Override
	public List<Product> getFilteredProductsByChannel(Channel channel, List<String> productListId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> getProductsByChannel(Channel channel, Pageable pageable) {
		return null;
	}

	@Override
	public void addProductsToChannel(Channel channel, List<String> productIdList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeProductsFromChannel(Channel channel, List<String> productIdList) {
		// TODO Auto-generated method stub

	}

}
