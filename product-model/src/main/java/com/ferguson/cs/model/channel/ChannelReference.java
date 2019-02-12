package com.ferguson.cs.model.channel;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChannelReference implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;
}
