package com.ferguson.cs.product.dao.channel;

import java.util.List;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.channel.Channel;

public class ChannelDataAccessImpl implements ChannelDataAccess {

	private ChannelMapper channelMapper;
	
	
	public ChannelDataAccessImpl(ChannelMapper channelMapper) {
		super();
		this.channelMapper = channelMapper;
	}

	@Override
	public List<Channel> findChannelList(IdCodeCriteria criteria) {
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
