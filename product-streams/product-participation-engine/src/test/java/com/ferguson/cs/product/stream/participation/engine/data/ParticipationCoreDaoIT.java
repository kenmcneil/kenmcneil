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

		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(52000)
						.saleId(2020)
						.build());

		participationTestUtilities.insertParticipationFixture(
				ParticipationItemFixture.builder()
						.participationId(53000)
						.saleId(3030)
						.build());

		participationCoreDao.setParticipationIsActive(52000, false);
		participationCoreDao.setParticipationIsActive(53000, true);

		Boolean isInactiveActivated = participationCoreDao.getParticipationIsActive(52000);
		Boolean isActiveActivated = participationCoreDao.getParticipationIsActive(53000);
		Assertions.assertThat(isInactiveActivated).isFalse();
		Assertions.assertThat(isActiveActivated).isTrue();
	}
}
