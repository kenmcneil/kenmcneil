package com.ferguson.cs.productapi.channel;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.product.Product;

public class ChannelServiceImpl implements ChannelService {

	@Override
	public Channel getChannel(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Channel> getChannelsByBusinessUnit(BusinessUnit businessUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel saveChannel(Channel channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteChannel(Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Product> getFilteredProductsByChannel(Channel channel, List<String> productListId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsByChannel(Channel channel, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProductsToChannel(Channel channel, List<String> productIdList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeProductsFromChannel(Channel channel, List<String> productIdList) {
		// TODO Auto-generated method stub

	}

}
