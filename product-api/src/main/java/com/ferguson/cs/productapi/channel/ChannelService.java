package com.ferguson.cs.productapi.channel;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.product.Product;

public interface ChannelService {

	Channel getChannel(String id);
	List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit);
	Channel saveChannel(Channel channel);
	void deleteChannel(Channel channel);

	List<Product> getFilteredProductsByChannel(Channel channel, List<String> productListId);
	List<Product> getProductsByChannel(Channel channel, Pageable pageable);
	void addProductsToChannel(Channel channel, List<String> productIdList);
	void removeProductsFromChannel(Channel channel, List<String> productIdList);
}
