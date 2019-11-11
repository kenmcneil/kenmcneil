package com.ferguson.cs.product.api.channel;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;

public interface ChannelService {

	Optional<Channel> getChannelByCode(String code);
	List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit);
	Channel saveChannel(Channel channel);
	void deleteChannel(Channel channel);
}
