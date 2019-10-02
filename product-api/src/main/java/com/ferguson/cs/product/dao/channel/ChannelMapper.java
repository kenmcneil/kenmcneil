package com.ferguson.cs.product.dao.channel;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.channel.Channel;

@Mapper
public interface ChannelMapper {

	List<Channel> findChannelList(IdCodeCriteria criteria);		
	int insertChannel(Channel channel);
	int updateChannel(Channel channel);
	int deleteChannel(Channel channel);
}
