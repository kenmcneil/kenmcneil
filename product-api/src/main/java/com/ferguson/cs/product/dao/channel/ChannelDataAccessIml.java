package com.ferguson.cs.product.dao.channel;

import java.util.List;

import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelCriteria;

public class ChannelDataAccessIml implements ChannelDataAccess {

	private ChannelMapper channelMapper;
	
	
	public ChannelDataAccessIml(ChannelMapper channelMapper) {
		super();
		this.channelMapper = channelMapper;
	}

	@Override
	public List<Channel> findChannelList(ChannelCriteria criteria) {
		return channelMapper.findChannelList(criteria);
	}

	@Override
	public Channel saveChannel(Channel channel) {
//		return channelRepository.save(channel);
		return null;
	}

	@Override
	public void deleteChannel(Channel channel) {
	}

}
