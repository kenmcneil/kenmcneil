package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	/**
	 * Use in tests to have the start date be automatically set relative to the simulation start date.
	 * Tip: for convenience use scheduleByDays() to set both start and end date offsets.
	 */
	private Integer startDateOffsetDays;

	/**
	 * Use in tests to have the end date be automatically set relative to the simulation start date.
	 * Tip: for convenience use scheduleByDays() to set both start and end date offsets.
	 */
	private Integer endDateOffsetDays;

	/**
	 * 	Use in tests to populate records in the participationProduct table. See builder method below.
 	 */
	private List<Integer> uniqueIds;

	/**
	 * Use in tests to specify a subset of the Participation's products that it is expected to own after activation.
	 * This can happen when there are overlapping Participations. This may be updated at specific points in a scenario
	 * as needed due to changes in ownership as overlapping participations are activated and deactivated.
	 *
	 * The default value of null indicates that all of the products in uniqueIds are expected to be owned at activation.
	 */
	private List<Integer> expectedOwnedUniqueIds;

	/**
	 * Use in tests to populate records in the participationCalculatedDiscount table.
	 */
	private List<ParticipationCalculatedDiscount> calculatedDiscounts;

	@Builder.Default
	@Setter(AccessLevel.PRIVATE)
	private List<LifecycleState> stateLog = new ArrayList<>();

	public String toString() {
		return String.format("Participation(id(%s), saleId(%s), products(%s), schedule(%s, %s))",
				participationId,
				saleId,
				StringUtils.join(uniqueIds, ", "),
				startDateOffsetDays != null ? startDateOffsetDays : startDate,
				endDateOffsetDays != null ? endDateOffsetDays : endDate
		);
	}

	public static class ParticipationItemFixtureBuilder {
		/**
		 * Helper to set both the start date and the end date to specific date values.
		 * Null values indicate there is no start/end date.
		 */
		public ParticipationItemFixtureBuilder schedule(Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
			return this;
		}

		/**
		 * Use in tests to have the start and end dates be automatically set relative to the simulation start date.
		 * A start or end date value here overrides any date values set.
		 * A null value indicates to not use the offset feature.
		 */
		public ParticipationItemFixtureBuilder scheduleByDays(Integer startOffsetDays, Integer endOffsetDays) {
			// Save the given offsets, and the actual schedule dates will be set to the current run date when fixture is inserted.
			this.startDateOffsetDays = startOffsetDays;
			this.endDateOffsetDays = endOffsetDays;
			return this;
		}

		/**
		 * For use in tests to populate uniqueIds. Values must not be null.
		 */
		public ParticipationItemFixtureBuilder uniqueIds(Integer... ids) {
			Assertions.assertThat(ids).allSatisfy(id -> Assertions.assertThat(id).isNotNull());
			this.uniqueIds = Arrays.asList(ids);
			return this;
		}

		/**
		 * For use in tests to populate uniqueIds. Values must not be null.
		 */
		public ParticipationItemFixtureBuilder expectedOwnedUniqueIds(Integer... ids) {
			Assertions.assertThat(ids).allSatisfy(id -> Assertions.assertThat(id).isNotNull());
			this.expectedOwnedUniqueIds = Arrays.asList(ids);
			return this;
		}
	}
}
