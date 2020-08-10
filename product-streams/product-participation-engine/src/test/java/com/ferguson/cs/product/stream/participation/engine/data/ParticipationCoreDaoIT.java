package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationEngineITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ParticipationCoreDaoIT extends ParticipationEngineITBase {
	@Autowired
	public ParticipationCoreDao participationCoreDao;

	@Test
	public void setParticipationIsActive_getParticipationIsActive() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder().saleId(2020).build();
		ParticipationItemFixture p2 = ParticipationItemFixture.builder().saleId(3030).build();
		participationTestUtilities.insertParticipationFixtures(p1, p2);

		participationCoreDao.setParticipationIsActive(p1.getParticipationId(), false);
		participationCoreDao.setParticipationIsActive(p2.getParticipationId(), true);

		Boolean isInactiveActivated = participationCoreDao.getParticipationIsActive(p1.getParticipationId());
		Boolean isActiveActivated = participationCoreDao.getParticipationIsActive(p2.getParticipationId());
		Assertions.assertThat(isInactiveActivated).isFalse();
		Assertions.assertThat(isActiveActivated).isTrue();
	}

	@Test
	public void insertParticipationItemPartialHistory_insertParticipationProductsHistory() {
		//arrange
		int tUniqueId = 123456;
		int tParticipationId = 10000;
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(tParticipationId)
				.saleId(1010)
				.startDate(new Date())
				.endDate(new Date())
				.lastModifiedUserId(1)
				.isActive(false)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.contentTypeId(ParticipationContentType.PARTICIPATION_COUPON_V1.contentTypeId())
				.build();
		List<Integer> uniqueIds = new ArrayList<>();
		uniqueIds.add(tUniqueId);

		//act
		participationCoreDao.insertParticipationItemPartialHistory(itemPartial);
		participationCoreDao.insertParticipationProductsHistory(itemPartial.getParticipationId(), uniqueIds);

		//assert
		int returnedUniqueId = participationTestUtilities.getHistoricalUniqueId(tParticipationId);
		Assertions.assertThat(returnedUniqueId).isEqualTo(tUniqueId);
	}
}
