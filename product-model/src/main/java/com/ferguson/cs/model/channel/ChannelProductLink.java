package com.ferguson.cs.model.channel;

import java.time.LocalDateTime;

import com.ferguson.cs.model.PersistentDocument;
import com.ferguson.cs.model.product.ProductReference;

import lombok.Builder;
import lombok.Data;

/**
 * A sales channel will have a distinct subset of products that are sold through that channel. This entity is used to map the channel to its list of products.
 */
@Data
@Builder
public class ChannelProductLink implements PersistentDocument {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistent ID of then channel/product link.
	 */
	private String id;

	private ChannelReference channelReference;
	private ProductReference productReference;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;
	private Long version;

}
