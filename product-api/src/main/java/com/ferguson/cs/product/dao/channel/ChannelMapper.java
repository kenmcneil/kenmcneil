package com.ferguson.cs.product.dao.channel;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelCriteria;

@Mapper
public interface ChannelMapper {

	List<Channel> findChannelList(ChannelCriteria criteria);
	int insertChannel(Channel channel);
	int updateChannel(Channel channel);
	int deleteChannel(Channel channel);
}
