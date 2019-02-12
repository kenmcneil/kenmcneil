package com.ferguson.cs.product.api.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.product.Product;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.product.api.taxonomy.TaxonomyService;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;

@Service
public class ChannelServiceImpl implements ChannelService {

	private final ChannelRepository channelRepository;
	private final TaxonomyService taxonomyService;


	public ChannelServiceImpl(ChannelRepository channelRepository, TaxonomyService taxonomyService) {
		this.channelRepository = channelRepository;
		this.taxonomyService = taxonomyService;
	}

	@Override
	public Channel getChannelByCode(String code) {
		Assert.hasText(code, "The code is required to retrieve the channel.");
		return channelRepository.findByCode(code);
	}

	@Override
	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		Assert.notNull(businessUnit, "The business unit is required to retrieve the channel list.");
		return channelRepository.findByBusinessUnit(businessUnit);
	}

	@Override
	public Channel saveChannel(Channel channel) {
		Assert.notNull(channel, "A channel was not specified.");
		Assert.hasText(channel.getCode(), "The chanel code is required.");
		Assert.notNull(channel.getBusinessUnit(), "The business unit is required.");

		if (channel.getIsActive() == null) {
			channel.setIsActive(Boolean.FALSE);
		}
		return channelRepository.save(channel);
	}

	@Override
	public void deleteChannel(final Channel channel) {
		Assert.notNull(channel, "A channel was not specified.");
		Assert.isTrue(StringUtils.hasText(channel.getId()), "The channel ID must be provided to delete the channel.");

		channelRepository.delete(channel);
		//TODO Need to delete all the channel/product links. Note: Mongo does not support transactions across document operations, so this is NOT atomic.
	}

	@Override
	public List<Taxonomy> getTaxonomiesByChannel(final Channel channel) {
		Assert.notNull(channel, "A channel was not specified.");
		Assert.isTrue(StringUtils.hasText(channel.getId()), "The channel ID must be provided to retrieve the taxonomy list.");
		Optional<Channel> retrievedChannel = channelRepository.findById(channel.getId());
		if (!retrievedChannel.isPresent()) {
			throw new ResourceNotFoundException("The channel [" + channel.getId() + "] could not be found.");
		}
		return taxonomyService.getTaxonomiesByReference(retrievedChannel.get().getTaxonomyReferenceList());

	}


	@Override
	public List<Product> getFilteredProductsByChannel(Channel channel, List<String> productListId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsByChannel(Channel channel, Pageable pageable) {
		// TODO Auto-generated method stub
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