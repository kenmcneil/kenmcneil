package com.ferguson.cs.product.api.channel;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;

@RestController
@RequestMapping("/channels")
public class ChannelController {

	private final ChannelService channelService;

	public ChannelController(ChannelService channelService) {
		this.channelService = channelService;
	}

	@GetMapping(value = "/{code}")
	public Channel getChannelByCode(String code) {
		return OptionalResourceHelper.handle(channelService.getChannelByCode(code), "channel", code);
	}

	@PostMapping(value = "")
	public Channel saveChannel(Channel channel) {
		return channelService.saveChannel(channel);
	}

	@DeleteMapping(value = "/{code}")
	public void deleteChannel(@PathVariable("code") String code) {
		Channel channel = getChannelByCode(code);
		channelService.deleteChannel(channel);
	}
}
