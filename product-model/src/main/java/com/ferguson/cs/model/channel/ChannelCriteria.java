package com.ferguson.cs.model.channel;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChannelCriteria {
	/**
	 * Unique persistent ID assigned to the channel.
	 */
	@Id
	private Integer channelId;

	/**
	 * A unique business key assigned to the channel.
	 *
	 * This value is required.
	 */
	private String channelCode;

	/**
	 * Search for channels associated with a business unit.
	 */
	private Integer businessUnitId;

}
