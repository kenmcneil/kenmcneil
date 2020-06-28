package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This bridges the ParticipationItem from Construct and the set of classes that
 * represents the Participation data normalized in the SQL database:
 *   -  participationProduct
 *   -  participationCalculatedDiscount
 *   -  participationItemPartial
 * This class is used in tests to make it easy to create test fixture data.
 */
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
	@Builder.Default
	private Boolean isSimulatedPublish = false;
	//LWH?????????????

	/**
	 * Controls how the ParticipationItem.content map is created. The map will be created based on
	 * the schema definition for the content type. Required unless simulatedPublish is true.
	 */
	private ParticipationContentType contentType;

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
	private List<CalculatedDiscountFixture> calculatedDiscountFixtures;

	/**
	 * Use in tests to populate records in the participationItemizedDiscount table.
	 */
	private List<ItemizedDiscountFixture> itemizedDiscountFixtures;

	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	@Builder.Default
	@Setter(AccessLevel.PRIVATE)
	private List<LifecycleState> stateLog = new ArrayList<>();

	public String toString() {
		return String.format("Participation(id(%s), type(%s), saleId(%s), products(%s), schedule(%s, %s)), contentType(%s)",
				participationId,
				contentType == ParticipationContentType.PARTICIPATION_V1
						? (CollectionUtils.isEmpty(uniqueIds) ? "SaleID" : "Calc")
						: "Line",
				saleId,
				StringUtils.join(uniqueIds, ", "),
				startDateOffsetDays != null ? startDateOffsetDays : startDate,
				endDateOffsetDays != null ? endDateOffsetDays : endDate,
				contentType
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

		/**
		 * For use in tests to populate calculated discounts. Values must not be null.
		 */
		public ParticipationItemFixtureBuilder calculatedDiscounts(CalculatedDiscountFixture... discountFixtures) {
			Assertions.assertThat(discountFixtures).allSatisfy(discountFixture -> {
				Assertions.assertThat(discountFixture).isNotNull();
				Assertions.assertThat(discountFixture.getPricebookId()).isNotNull();
				Assertions.assertThat(discountFixture.getDiscountAmount()).isNotNull();
				Assertions.assertThat(discountFixture.getIsPercent()).isNotNull();
			});
			this.calculatedDiscountFixtures = Arrays.asList(discountFixtures);
			return this;
		}

		/**
		 * For use in tests to populate itemized discounts. Values must not be null.
		 */
		public ParticipationItemFixtureBuilder itemizedDiscounts(ItemizedDiscountFixture... discountFixtures) {
			Assertions.assertThat(discountFixtures).allSatisfy(discountFixture -> {
				Assertions.assertThat(discountFixture).isNotNull();
				Assertions.assertThat(discountFixture.getPricebook1Price()).isNotNull();
				Assertions.assertThat(discountFixture.getPricebook22Price()).isNotNull();
			});
			this.itemizedDiscountFixtures = Arrays.asList(discountFixtures);
			return this;
		}

		public ParticipationItemFixtureBuilder simulatedPublish() {
			this.isSimulatedPublish = true;
			return this;
		}
	}
}
