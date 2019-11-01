package com.ferguson.cs.product.dao.channel;

import java.util.List;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.channel.Channel;

public interface ChannelDataAccess {

	List<Channel> findChannelList(IdCodeCriteria criteria);
	Channel saveChannel(Channel channel);
	void deleteChannel(Channel channel);

}
