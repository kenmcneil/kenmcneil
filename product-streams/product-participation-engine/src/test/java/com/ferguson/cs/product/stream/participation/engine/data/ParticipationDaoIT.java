package com.ferguson.cs.product.stream.participation.engine.data;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.BaseParticipationEngineIT;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ParticipationDaoIT extends BaseParticipationEngineIT {
	@Autowired
	public ParticipationDao participationDao;

	@Test
	public void setParticipationIsActive_active_inactive() {
		ParticipationItemFixture values = new ParticipationItemFixture();
		values.setParticipationId(50000);
		values.setSaleId(2020);
		values.setIsActive(false);
		participationTestUtilities.insertParticipation(values);
		int rowsModified = participationDao.setParticipationIsActive(50000, true);
		Assertions.assertThat(rowsModified).isEqualTo(1);
	}
}
