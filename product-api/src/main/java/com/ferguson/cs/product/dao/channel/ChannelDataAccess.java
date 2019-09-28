package com.ferguson.cs.product.dao.channel;

import java.util.List;

import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelCriteria;

public interface ChannelDataAccess {

	List<Channel> findChannelList(ChannelCriteria criteria);
	Channel saveChannel(Channel channel);
	void deleteChannel(Channel channel);

}
