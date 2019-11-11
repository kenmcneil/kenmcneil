package com.ferguson.cs.product.dao.channel;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.data.AbstractDataAccess;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelCriteria;

@Repository
public class ChannelDataAccessImpl extends AbstractDataAccess  implements ChannelDataAccess {

	private ChannelMapper channelMapper;


	public ChannelDataAccessImpl(ChannelMapper channelMapper) {
		super();
		this.channelMapper = channelMapper;
	}

	@Override
	public List<Channel> findChannels(ChannelCriteria criteria) {
		return channelMapper.findChannelList(criteria);
	}

	@Override
	@Transactional
	public Channel saveChannel(Channel channel) {
		return saveEntity(channel, channelMapper::insertChannel, channelMapper::updateChannel);
	}

	@Override
	public void deleteChannel(Channel channel) {
		deleteEntity(channel, channelMapper::deleteChannel);
	}

}
