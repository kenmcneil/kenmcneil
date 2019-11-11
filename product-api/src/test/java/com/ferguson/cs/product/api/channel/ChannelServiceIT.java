package com.ferguson.cs.product.api.channel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static com.ferguson.cs.product.test.GeneralTestUtilities.randomString;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.channel.BusinessUnit;
import com.ferguson.cs.model.channel.Channel;
import com.ferguson.cs.model.channel.ChannelType;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.product.test.BaseProductIT;

public class ChannelServiceIT extends BaseProductIT {

	@Autowired
	ChannelService channelService;

	@Test
	public void saveChannel_asserts() {
		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(null)
			);
		//Null Code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.build())
			);

		//Empty Code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.code("")
						.build())
			);

		//No business unit.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.code("YOYOYO")
						.build())
			);

		//No channel type.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.code("YOYOYO")
						.businessUnit(BusinessUnit.BUILD)
						.build())
			);

		//Null taxonomy
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.code("YOYOYO")
						.businessUnit(BusinessUnit.BUILD)
						.channelType(ChannelType.WEB_STORE)
						.build())
			);

		//Empty taxonomy
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> channelService.saveChannel(Channel.builder()
						.code("YOYOYO")
						.businessUnit(BusinessUnit.BUILD)
						.channelType(ChannelType.WEB_STORE)
						.taxonomy(new Taxonomy())
						.build())
			);
	}

	@Test
	public void saveChannel_insert() {

		Taxonomy taxonomy = taxonomyTestUtilities().insertTaxonomy("MYTRUNK", "Trunk taxonomy");
		Channel channel = Channel.builder()
				.businessUnit(BusinessUnit.BUILD)
				.channelType(ChannelType.WEB_STORE)
				.code(randomString(8))
				.description("Out of the back of my truck sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();

		channel = channelService.saveChannel(channel);

		assertThat(channel.getId()).isNotNull();
		assertThat(channel.getVersion()).isEqualTo(1);
		assertThat(channel.getCreatedTimestamp()).isNotNull();
		assertThat(channel.getLastModifiedTimestamp()).isNotNull();

		//Test duplicate key :
		final Channel channel2 = channel;
		channel2.setId(null);
		assertThatExceptionOfType(DataAccessException.class).isThrownBy(
				() -> channelService.saveChannel(channel2)
			);
	}

	@Test
	public void saveChannel_update() {

		Taxonomy taxonomy = taxonomyTestUtilities().insertTaxonomy("MYTRUNK", "Trunk taxonomy");
		Channel channel = Channel.builder()
				.businessUnit(BusinessUnit.BUILD)
				.channelType(ChannelType.WEB_STORE)
				.code(randomString(8))
				.description("Out of the back of my truck sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();

		channel = channelService.saveChannel(channel);
		channel.setDescription("My sketchy trunk sales channel");
		channel = channelService.saveChannel(channel);

		//Make sure the updated description is in the database.
		Optional<Channel> retrievedChannel = channelService.getChannelByCode(channel.getCode());
		assertThat(retrievedChannel).isNotEmpty();
		channel = retrievedChannel.get();
		assertThat(channel.getVersion()).isEqualTo(2);
		assertThat(channel.getDescription()).isEqualTo("My sketchy trunk sales channel");
		assertThat(channel.getLastModifiedTimestamp()).isNotEqualTo(channel.getCreatedTimestamp());

		//Test optimistic record locking
		final Channel channelOld = channel;
		channelOld.setVersion(1);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> channelService.saveChannel(channelOld)
			);
	}

	@Test
	public void getChannelByCode() {

		Taxonomy taxonomy = taxonomyTestUtilities().insertTaxonomy("MYTRUNK", "Trunk taxonomy");
		Channel channel = Channel.builder()
				.businessUnit(BusinessUnit.SUPPLY)
				.channelType(ChannelType.MARKETPLACE)
				.code(randomString(8))
				.description("Out of the back of my truck sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();

		channel = channelService.saveChannel(channel);
		Optional<Channel> retrievedChannel = channelService.getChannelByCode(channel.getCode());
		assertThat(retrievedChannel).isNotEmpty();

		channel = retrievedChannel.get();
		assertThat(channel.getVersion()).isEqualTo(1);
		assertThat(channel.getBusinessUnit()).isEqualTo(BusinessUnit.SUPPLY);
		assertThat(channel.getChannelType()).isEqualTo(ChannelType.MARKETPLACE);
		assertThat(channel.getDescription()).isEqualTo("Out of the back of my truck sales channel");
		assertThat(channel.getIsActive()).isTrue();
		assertThat(channel.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
	}

	@Test
	public void getChannelsByBusinessUnit() {
		Taxonomy taxonomy = taxonomyTestUtilities().insertTaxonomy("MYTRUNK", "Trunk taxonomy");
		Channel channel1 = Channel.builder()
				.businessUnit(BusinessUnit.SUPPLY)
				.channelType(ChannelType.MARKETPLACE)
				.code(randomString(8))
				.description("Out of the back of my truck sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();

		Channel channel2 = Channel.builder()
				.businessUnit(BusinessUnit.SUPPLY)
				.channelType(ChannelType.WEB_STORE)
				.code(randomString(8))
				.description("Out of the back of my Fiat sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();

		Channel channel3 = Channel.builder()
				.businessUnit(BusinessUnit.BUILD)
				.channelType(ChannelType.MARKETPLACE)
				.code(randomString(8))
				.description("Out of the back of my Dodge Dart sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();
		channel1 = channelService.saveChannel(channel1);
		channel2 = channelService.saveChannel(channel2);
		channel3 = channelService.saveChannel(channel3);

		List<Channel> retrievedChannels = channelService.getChannelsByBusinessUnit(BusinessUnit.SUPPLY);
		assertThat(retrievedChannels).hasSize(2);

		for (Channel channel : retrievedChannels) {

			if (channel.getId().equals(channel1.getId())) {
				assertThat(channel.getVersion()).isEqualTo(1);
				assertThat(channel.getBusinessUnit()).isEqualTo(BusinessUnit.SUPPLY);
				assertThat(channel.getChannelType()).isEqualTo(ChannelType.MARKETPLACE);
				assertThat(channel.getDescription()).isEqualTo("Out of the back of my truck sales channel");
				assertThat(channel.getIsActive()).isTrue();
				assertThat(channel.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
			} else {
				assertThat(channel.getVersion()).isEqualTo(1);
				assertThat(channel.getBusinessUnit()).isEqualTo(BusinessUnit.SUPPLY);
				assertThat(channel.getChannelType()).isEqualTo(ChannelType.WEB_STORE);
				assertThat(channel.getDescription()).isEqualTo("Out of the back of my Fiat sales channel");
				assertThat(channel.getIsActive()).isTrue();
				assertThat(channel.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
			}
		}
	}

	@Test
	public void deleteChannel() {
		Taxonomy taxonomy = taxonomyTestUtilities().insertTaxonomy("MYTRUNK", "Trunk taxonomy");
		Channel channel = Channel.builder()
				.businessUnit(BusinessUnit.SUPPLY)
				.channelType(ChannelType.MARKETPLACE)
				.code(randomString(8))
				.description("Out of the back of my truck sales channel")
				.isActive(true)
				.taxonomy(taxonomy)
				.build();
		channel = channelService.saveChannel(channel);

		//Test optimistic record locking
		final Channel channelOld = channel;
		channelOld.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> channelService.deleteChannel(channelOld)
			);

		//Now test valid delete.
		Optional<Channel> retrievedChannel = channelService.getChannelByCode(channel.getCode());
		assertThat(retrievedChannel).isNotEmpty();
		channelService.deleteChannel(retrievedChannel.get());
		retrievedChannel = channelService.getChannelByCode(channel.getCode());
		assertThat(retrievedChannel).isEmpty();
	}

}
