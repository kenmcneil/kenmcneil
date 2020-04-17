package com.ferguson.cs.product.stream.participation.engine.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.test.BaseParticipationEngineIT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.product.stream.participation.engine"})
@Transactional
public class ParticipationDaoIT extends BaseParticipationEngineIT {
	@Autowired
	ParticipationDao participationDao;

	@Test
	public void setParticipationIsActive_active_inactive() {
//		ParticipationItemFixture values = new ParticipationItemFixture();
//		values.setParticipationId(5000);
//		values.setIsActive(false);
//		insertParticipation(values);
//		int rowsModified = participationDao.setParticipationIsActive(5000, true);
//		Assertions.assertThat(rowsModified).isEqualTo(1);
	}
}
