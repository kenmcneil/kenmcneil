package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationItemFixture {

	private Integer participationId;
	private Date startDate;
	private Date endDate;
	private Integer lastModifiedUserId;
	@Builder.Default
	private Boolean isActive = false;
	@Builder.Default
	private Integer saleId = 0;

	private Integer startDateOffsetDays;
	private Integer endDateOffsetDays;

	private List<Integer> uniqueIds;
	private List<ParticipationProductFixture> products;
	private List<ParticipationCalculatedDiscountsFixture> calculatedDiscounts;

	public static class ParticipationItemFixtureBuilder {
		public ParticipationItemFixtureBuilder schedule(Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
			return this;
		}

		/**
		 * Uses the given offsets to calculate the startDate and endDate for the Participation fixture
		 * based on the simulated date when the scenario starts running.
		 */
		public ParticipationItemFixtureBuilder scheduleByDays(Integer startOffsetDays, Integer endOffsetDays) {
			// Save the given offsets, and the actual schedule dates will be set to the current run date when fixture is inserted.
			this.startDateOffsetDays = startOffsetDays;
			this.endDateOffsetDays = endOffsetDays;
			return this;
		}
	}
}
