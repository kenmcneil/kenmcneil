package com.ferguson.cs.product.api.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelCriteria;
import com.ferguson.cs.product.dao.channel.ChannelDataAccess;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class ChannelServiceImpl implements ChannelService {

	private final ChannelDataAccess channelDataAccess;

	public ChannelServiceImpl(ChannelDataAccess channelDataAccess) {
		this.channelDataAccess = channelDataAccess;
	}

	@Override
	public Optional<Channel> getChannelByCode(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");

		List<Channel> results = channelDataAccess.findChannels(ChannelCriteria.builder()
			.channelCode(code)
			.build());

		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}
	}

	@Override
	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		ArgumentAssert.notNull(businessUnit, "business unit");
		return channelDataAccess.findChannels(ChannelCriteria.builder()
				.businessUnit(businessUnit)
				.build());	}

	@Override
	public Channel saveChannel(Channel channel) {
		ArgumentAssert.notNull(channel, "channel");
		ArgumentAssert.notNullOrEmpty(channel.getCode(), "channel.code");
		ArgumentAssert.notNull(channel.getBusinessUnit(), "channel.businessUnit");
		ArgumentAssert.notNull(channel.getChannelType(), "channel.channelType");
		ArgumentAssert.notNull(channel.getTaxonomy(), "channel.taxonomy");
		ArgumentAssert.notNull(channel.getTaxonomy().getId(), "channel.taxonomy.id");

		if (channel.getIsActive() == null) {
			channel.setIsActive(Boolean.FALSE);
		}
		return channelDataAccess.saveChannel(channel);
	}

	@Override
	public void deleteChannel(final Channel channel) {
		ArgumentAssert.notNull(channel, "channel");
		ArgumentAssert.notNull(channel.getId(), "channel.id");
		ArgumentAssert.notNull(channel.getVersion(), "channel.version");
		channelDataAccess.deleteChannel(channel);
	}
}
