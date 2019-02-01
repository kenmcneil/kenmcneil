package com.ferguson.cs.productapi.channel;

import java.util.List;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;

public interface ChannelService {

	Channel getChannel(String id);
	List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit);
	Channel saveChannel(Channel channel);
	void deleteChannel(Channel channel);


}
