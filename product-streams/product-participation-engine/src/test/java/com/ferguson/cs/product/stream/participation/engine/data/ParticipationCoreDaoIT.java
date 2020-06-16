package com.ferguson.cs.product.stream.participation.engine.data;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
