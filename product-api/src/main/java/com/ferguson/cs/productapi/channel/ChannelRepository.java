package com.ferguson.cs.productapi.channel;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;

public interface ChannelRepository extends MongoRepository<Channel, String> {
	Channel findByBusinessUnit(BusinessUnit businessUnit);
}
