package com.ferguson.cs.model.channel;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.ferguson.cs.model.product.Product;

import lombok.Builder;
import lombok.Value;

/**
 * A sales channel will have a distinct subset of products that are sold through that channel. This entity is used to map the channel to its list of products.
 */
@Value
@Builder
public class ChannelProductLink {

	private String channelCode;

	@DBRef
	private Product product;

}
