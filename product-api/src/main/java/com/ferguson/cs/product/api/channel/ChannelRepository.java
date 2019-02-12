package com.ferguson.cs.product.api.channel;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;

public interface ChannelRepository extends MongoRepository<Channel, String> {
	Channel findByCode(String code);
	List<Channel> findByBusinessUnit(BusinessUnit businessUnit);
}