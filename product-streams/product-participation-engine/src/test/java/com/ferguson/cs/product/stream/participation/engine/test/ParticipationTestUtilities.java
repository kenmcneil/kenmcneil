package com.ferguson.cs.product.stream.participation.engine.test;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationProduct;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationCouponV1TestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationItemizedV1V2TestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.ParticipationV1V2TestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.PricebookCost;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductModified;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;
import com.ferguson.cs.product.stream.participation.engine.test.model.WasPriceFixture;

public class ParticipationTestUtilities {
	public static final int TEST_USERID = 1234;

	public static final String INSERT_PARTICIPATION_ITEM_PARTIAL_SQL =
			"INSERT INTO mmc.product.participationItemPartial" +
					" (participationId, saleId, startDate, endDate, lastModifiedUserId, isActive, contentTypeId," +
					" isCoupon, shouldBlockDynamicPricing)" +
					" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_PRODUCT =
			"INSERT INTO mmc.product.participationProduct VALUES" +
					" (:participationId, :uniqueId, :isOwner)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT =
			"INSERT INTO mmc.product.participationCalculatedDiscount" +
					" (participationId, priceBookId, changeValue, isPercent, templateId)" +
					" VALUES (?, ?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_ITEMIZED_DISCOUNT =
			"INSERT INTO mmc.product.participationItemizedDiscount" +
					" (participationId, uniqueid, pricebookId, price)" +
					" VALUES (?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE =
			"INSERT INTO mmc.product.participationCalculatedDiscountTemplate" +
					" (templateTypeId, template, description, active)" +
					" VALUES (?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_TYPE =
			"INSERT INTO mmc.product.participationCalculatedDiscountTemplateType" +
					" (templateType)" +
					" VALUES (?)";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationCalculatedDiscount" +
					" WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_ITEMIZED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationItemizedDiscount" +
					" WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationProduct" +
					" WHERE participationId = ?";

	public static final String SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.product.sale WHERE participationId = ?";

	public static final String SELECT_PRICEBOOK_COST_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.dbo.pricebook_cost WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE =
			"SELECT TOP 1 id FROM mmc.product.participationCalculatedDiscountTemplate";

	public static final String SELECT_PARTICIPATION_PARTIAL_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationItemPartial" +
					" WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_BY_PARTICIPATIONID =
			"SELECT participationId, uniqueId, isOwner FROM mmc.product.participationProduct" +
					" WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID =
			"SELECT participationId, priceBookId, changeValue, isPercent, templateId" +
					" FROM mmc.product.participationCalculatedDiscount" +
					" WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_ITEMIZED_DISCOUNT_BY_PARTICIPATIONID =
			"SELECT participationId, uniqueId, pricebookId, price" +
					" FROM mmc.product.participationItemizedDiscount" +
					" WHERE participationId = ?";

	public static final String SELECT_PRODUCT_SALE_LINK_BY_UNIQUE_ID =
			"SELECT * FROM mmc.product.sale WHERE uniqueId IN ( :uniqueIds )";

	public static final String SELECT_PRODUCT_MODIFIED_BY_UNIQUE_ID =
			"SELECT * FROM mmc.product.modified WHERE uniqueId IN ( :uniqueIds )";

	private static final String SELECT_COALESCED_WASPRICE =
			"SELECT COALESCE(losPB1.wasPrice, wasPrice.wasPrice, 0.00)" +
					" FROM mmc.dbo.pricebookwasprice AS wasPrice\n" +
					" LEFT JOIN mmc.product.participationLastOnSale AS losPB1" +
					" ON losPB1.uniqueId = wasPrice.uniqueId" +
					" AND losPB1.pricebookId = 1" +
					" WHERE wasPrice.uniqueId = ?";

	private static final String SELECT_COALESCED_PREVIOUS_BASEPRICE =
			"SELECT COALESCE(losPB1.basePrice, PB1Cost.basePrice)" +
					" FROM mmc.dbo.pricebook_cost AS PB1Cost" +
					" LEFT JOIN mmc.product.participationLastOnSale AS losPB1" +
					" ON PB1Cost.uniqueId = losPB1.uniqueId" +
					" AND PB1Cost.pricebookId = losPB1.uniqueId" +
					" WHERE PB1Cost.uniqueId = ?" +
					" AND PB1Cost.pricebookId = 1";

	public static final String UPDATE_PRICEBOOK_COST_COST =
			"UPDATE mmc.dbo.PriceBook_Cost SET cost = ? WHERE UniqueId = ? AND PriceBookId = ?";

	public static final String UPDATE_PRICEBOOKWASPRICE_WASPRICE =
			"UPDATE mmc.dbo.pricebookWasPrice SET wasPrice = ? WHERE uniqueId = ?";

	public static final String SELECT_PRICEBOOKCOST_BASEPRICE =
		"SELECT basePrice" +
				" FROM mmc.dbo.PriceBook_Cost" +
				" WHERE uniqueId = ? AND pricebookId = ?";

	public static final String SELECT_PRICEBOOK_COST_BY_UNIQUEID_PRICEBOOKID =
			"SELECT uniqueId, pricebookId, cost, multiplier, basePrice, userId, participationId, wasPrice" +
					" FROM mmc.dbo.PriceBook_Cost" +
					" WHERE uniqueId IN ( :uniqueIds ) AND pricebookId IN ( :pricebookIds )" +
					" ORDER BY uniqueId, pricebookId";

	public static final String UPDATE_PARTICIPATION_LASTONSALE =
			"UPDATE mmc.product.participationLastOnSale" +
					" SET saleDate = ?, basePrice = ?" +
					" WHERE uniqueId= ? AND pricebookId = ?";

	public static final String INSERT_PARTICIPATION_LASTONSALE =
			"INSERT INTO mmc.product.participationLastOnSale (pricebookId, uniqueId, saleDate, basePrice)" +
					" VALUES (?, ?, ?, ?)";

	public static final String SELECT_LASTONSALE_BASEPRICE =
			"DECLARE @BasePrice DECIMAL(18,2);" +
					" SELECT @BasePrice = 0.0;" +
					" SELECT @BasePrice = basePrice" +
					" FROM mmc.product.participationLastOnSale" +
					" WHERE uniqueId = ? AND pricebookId = ?" +
					" SELECT @BasePrice";

	private static final String SELECT_HISTORICAL_UNIQUEID =
			"SELECT TOP 1 pph.uniqueId" +
					" FROM logs.dbo.participationItemPartialHistory AS ph\n" +
					" LEFT JOIN logs.dbo.participationProductHistory AS pph" +
					" ON ph.id = pph.participationItemPartialHistoryId" +
					" WHERE ph.participationId = ?";

	private static final String SELECT_HISTORICAL_ITEMIZED_PRICE =
			"SELECT TOP 1 price" +
			" FROM logs.dbo.participationItemizedDiscountHistory AS iDHis" +
			" JOIN logs.dbo.participationProductHistory AS pph" +
			" ON iDHis.uniqueId = pph.uniqueId" +
			" AND iDHis.participationItemPartialHistoryId = pph.participationItemPartialHistoryId" +
			" JOIN logs.dbo.participationItemPartialHistory AS ph" +
			" ON ph.id = pph.participationItemPartialHistoryId" +
			" WHERE ph.participationId = ? AND iDHis.uniqueId = ? AND iDHis.pricebookId = 1";

	private static final String SELECT_HISTORICAL_CALCULATED_CHANGE_VALUE = "SELECT TOP 1 changeValue" +
			" FROM logs.dbo.participationCalculatedDiscountHistory AS cDHis" +
			" JOIN logs.dbo.participationProductHistory AS pph" +
			" ON cDHis.participationItemPartialHistoryId = pph.participationItemPartialHistoryId" +
			" JOIN logs.dbo.participationItemPartialHistory AS ph" +
			" ON ph.id = pph.participationItemPartialHistoryId" +
			" WHERE ph.participationId = ? AND cDHis.pricebookId = 1";

	// modified from https://stackoverflow.com/a/16390624/9488171
	private static <T> ResultSetExtractor<T> singletonExtractor(RowMapper<? extends T> mapper) {
		return rs -> rs.next() ? mapper.mapRow(rs, 1) : null;
	}

	private static final ResultSetExtractor<ParticipationItemPartial> SINGLETON_PARTICIPATION_ITEM_FIXTURE_EXTRACTOR =
			singletonExtractor(new BeanPropertyRowMapper<>(ParticipationItemPartial.class));

	/**
	 * Helper to stream a possibly-null collection.
	 */
	public static <T> Stream<T> nullSafeStream(Collection<T> collection) {
		return Optional.ofNullable(collection)
				.map(Collection::stream)
				.orElseGet(Stream::empty);
	}

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final int firstTestParticipationId;
	private final int[] TEST_UNIQUE_IDS = {100, 101, 102, 103, 104, 105};
	private int nextTestParticipationId;

	public ParticipationTestUtilities(ParticipationEngineSettings participationEngineSettings, JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		this.firstTestParticipationId = participationEngineSettings.getTestModeMinParticipationId();
		this.nextTestParticipationId = firstTestParticipationId;
	}

	/**
	 * Ensure there's a calculated discount template record and return its id.
	 */
	public int getDefaultCalculatedDiscountTemplateId() {
		Integer templateId = jdbcTemplate.queryForObject(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE,
				Integer.class);
		if (templateId == null) {
			int templateTypeId = insert(INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_TYPE,
					"TEST_TEMPLATE_TYPE").intValue();
			templateId = insert(INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE,
					templateTypeId, "TEST GET ${percent}% OFF!", "TEST GET __% OFF!", true).intValue();
		}
		return templateId;
	}

	/**
	 * Returns a new test participation fixture id. Participation ids are not auto ids in the
	 * participationItemPartial table.
	 */
	public int getFirstTestParticipationId() {
		return firstTestParticipationId;
	}

	/**
	 * Returns a new test participation fixture id. Participation ids are not auto ids in the
	 * participationItemPartial table.
	 */
	public int getNextTestParticipationId() {
		return nextTestParticipationId++;
	}

	/**
	 * Return list of discontinued product unique ids that won't be used in real life. These probably won't be
	 * in the participationProduct table already when tests run.
	 */
	public int[] getSafeTestUniqueIds() {
		return TEST_UNIQUE_IDS;
	}

	/**
	 * Validate values and fill in any allowed null values with defaults.
	 * Validate that the participationId is either null or not in use yet. If null, an id is generated.
	 * Validate calculated discounts to ensure their participationId values are either null or same as the
	 * fixture's participationId. If null, use the fixture's participationId.
	 */
	public void validateAndSetDefaults(ParticipationItemFixture fixture) {
		if (fixture.getParticipationId() == null) {
			fixture.setParticipationId(getNextTestParticipationId());
		}
		if (fixture.getLastModifiedUserId() == null) {
			fixture.setLastModifiedUserId(TEST_USERID);
		}
		if (fixture.getIsActive() == null) {
			fixture.setIsActive(false);
		}
		if (!CollectionUtils.isEmpty(fixture.getCalculatedDiscountFixtures())) {
			fixture.getCalculatedDiscountFixtures().forEach(discountFixture -> {
				if (discountFixture.getTemplateId() == null) {
					discountFixture.setTemplateId(getDefaultCalculatedDiscountTemplateId());
				}
			});
		}
	}

	/**
	 * Inserts all the data from the fixture into the appropriate tables.
	 * This is similar to a publish operation.
	 *
	 * Defaults lastModifiedUserId to test user id if none specified.
	 * Defaults isActive, shouldBlockDynamicPricing and isCoupon to false if not specified.
	 * Defaults contentTypeId to 1 and "calculated discounts" if not specified.
	 */
	public void insertParticipationFixture(ParticipationItemFixture fixture) {
		validateAndSetDefaults(fixture);

		int participationId = fixture.getParticipationId();
		ParticipationContentType contentType = fixture.getContentType() != null
				? fixture.getContentType()
				: ParticipationContentType.PARTICIPATION_V1;
		List<Integer> uniqueIds = new ArrayList<>();

		// Insert participationItemPartial record.
		jdbcTemplate.update(INSERT_PARTICIPATION_ITEM_PARTIAL_SQL,
				participationId,
				fixture.getSaleId(),
				fixture.getStartDate(),
				fixture.getEndDate(),
				fixture.getLastModifiedUserId(),
				fixture.getIsActive(),
				contentType.contentTypeId(),
				fixture.getIsCoupon() == null ? 0 : fixture.getIsCoupon(),
				fixture.getShouldBlockDynamicPricing() == null ? 0 : fixture.getShouldBlockDynamicPricing()
		);

		// Insert discount records where applicable, and any uniqueIds as participationProduct
		// records with isOwner = false.
		if (contentType == ParticipationContentType.PARTICIPATION_V1 || contentType == ParticipationContentType.PARTICIPATION_V2) {
			nullSafeStream(fixture.getCalculatedDiscountFixtures())
					.map(discountFixture -> discountFixture.toParticipationCalculatedDiscount(participationId))
					.forEach(discount -> jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
							discount.getParticipationId(),
							discount.getPricebookId(),
							discount.getChangeValue(),
							discount.getIsPercent(),
							discount.getTemplateId())
					);
			uniqueIds = ParticipationV1V2TestLifecycle.getUniqueIds(fixture);

		} else if (
				contentType == ParticipationContentType.PARTICIPATION_ITEMIZED_V1
				|| contentType == ParticipationContentType.PARTICIPATION_ITEMIZED_V2
		) {
			// Insert any participationItemizedDiscount records, provided a list of lists where the inner list
			// is [[uniqueid, 1, pb1DiscountPrice], [uniqueId, 22, pb22DiscountPrice]]
			nullSafeStream(fixture.getItemizedDiscountFixtures())
					.map(discountFixture -> discountFixture.toParticipationItemizedDiscounts(participationId))
					.forEach(discount -> {
						jdbcTemplate.update(INSERT_PARTICIPATION_ITEMIZED_DISCOUNT, participationId,
								discount.get(0).getUniqueId(), discount.get(0).getPricebookId(),
								discount.get(0).getPrice());
						jdbcTemplate.update(INSERT_PARTICIPATION_ITEMIZED_DISCOUNT, participationId,
								discount.get(1).getUniqueId(), discount.get(1).getPricebookId(),
								discount.get(1).getPrice());
					});
			uniqueIds = ParticipationItemizedV1V2TestLifecycle.getUniqueIdsFromItemizedDiscounts(fixture);
		} else if (contentType == ParticipationContentType.PARTICIPATION_COUPON_V1) {
			uniqueIds = ParticipationCouponV1TestLifecycle.getUniqueIds(fixture);
		}

		if (!CollectionUtils.isEmpty(uniqueIds)) {
			SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(
					uniqueIds.stream()
							.map(uniqueId -> new ParticipationProduct(participationId, uniqueId, false))
							.collect(Collectors.toList())
			);
			namedParameterJdbcTemplate.batchUpdate(INSERT_PARTICIPATION_PRODUCT, batch);
		}
	}

	public void insertParticipationFixtures(ParticipationItemFixture ...fixtures) {
		for (ParticipationItemFixture fixture : fixtures) {
			insertParticipationFixture(fixture);
		}
	}

	public Integer getParticipationCalculatedDiscountCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID,
				Integer.class, participationId);
	}

	public Integer getParticipationItemizedDiscountCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATION_ITEMIZED_DISCOUNT_COUNT_BY_PARTICIPATIONID,
				Integer.class, participationId);
	}

	public Integer getParticipationProductCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATION_PRODUCT_COUNT_BY_PARTICIPATIONID,
				Integer.class, participationId);
	}

	public Integer getParticipationSaleIdCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID,
				Integer.class, participationId);
	}

	public Integer getPricebookCostParticipationCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PRICEBOOK_COST_COUNT_BY_PARTICIPATIONID,
				Integer.class, participationId);
	}

	/**
	 * Returns participationItemPartial row if record is found else null.
	 */
	public ParticipationItemPartial getParticipationItemPartial(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPATION_PARTIAL_BY_PARTICIPATIONID,
				SINGLETON_PARTICIPATION_ITEM_FIXTURE_EXTRACTOR, participationId);
	}

	public List<ParticipationProduct> getParticipationProducts(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPATION_PRODUCT_BY_PARTICIPATIONID,
				BeanPropertyRowMapper.newInstance(ParticipationProduct.class),
				participationId);
	}

	public List<ParticipationCalculatedDiscount> getParticipationCalculatedDiscounts(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID,
				BeanPropertyRowMapper.newInstance(ParticipationCalculatedDiscount.class),
				participationId);
	}

	public List<ParticipationItemizedDiscount> getParticipationItemizedDiscounts(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPATION_ITEMIZED_DISCOUNT_BY_PARTICIPATIONID,
				BeanPropertyRowMapper.newInstance(ParticipationItemizedDiscount.class),
				participationId);
	}

	public List<ProductSaleParticipation> getProductSaleParticipations(List<Integer> uniqueIds) {
		return uniqueIds.size() == 0
				? Collections.emptyList()
				: namedParameterJdbcTemplate.query(
						SELECT_PRODUCT_SALE_LINK_BY_UNIQUE_ID,
						new MapSqlParameterSource("uniqueIds", uniqueIds),
						BeanPropertyRowMapper.newInstance(ProductSaleParticipation.class)
				);
	}

	public ProductSaleParticipation getProductSaleParticipation(int uniqueId) {
		List<ProductSaleParticipation> sales = getProductSaleParticipations(Collections.singletonList(uniqueId));
		return sales.size() > 0 ? sales.get(0) : null;
	}

	public List<ProductModified> getProductModifieds(List<Integer> uniqueIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueIds", uniqueIds);
		return namedParameterJdbcTemplate.query(SELECT_PRODUCT_MODIFIED_BY_UNIQUE_ID,
				namedParameters, BeanPropertyRowMapper.newInstance(ProductModified.class));
	}

	// Returns results ordered by uniqueId then pricebookId.
	public List<PricebookCost> getPricebookCostsInOrder(List<Integer> uniqueIds, List<Integer> pricebookIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueIds", uniqueIds)
				.addValue("pricebookIds", pricebookIds);
		return namedParameterJdbcTemplate.query(SELECT_PRICEBOOK_COST_BY_UNIQUEID_PRICEBOOKID,
				namedParameters, BeanPropertyRowMapper.newInstance(PricebookCost.class));
	}

	public Double getCoalescedWasPrice(Integer uniqueId) {
		return jdbcTemplate.queryForObject(SELECT_COALESCED_WASPRICE, Double.class, uniqueId);
	}

	public Double getCoalescedPrevBasePrice(Integer uniqueId) {
		return jdbcTemplate.queryForObject(SELECT_COALESCED_PREVIOUS_BASEPRICE, Double.class, uniqueId);
	}

	public void upsertParticipationLastOnSaleBase(int pricebookId, int uniqueId, Date saleDate,
												  Double basePrice) {
		Double existingLOSBasePrice = jdbcTemplate.queryForObject(SELECT_LASTONSALE_BASEPRICE,
				Double.class, uniqueId, pricebookId);
		if (existingLOSBasePrice != 0) {
			jdbcTemplate.update(UPDATE_PARTICIPATION_LASTONSALE, saleDate, basePrice, uniqueId, pricebookId);
		} else {
			jdbcTemplate.update(INSERT_PARTICIPATION_LASTONSALE, pricebookId, uniqueId, saleDate, basePrice);
		}
	}

	public void updatePricebookCostCost(Double cost, int uniqueId, int pricebookId) {
		jdbcTemplate.update(UPDATE_PRICEBOOK_COST_COST, cost, uniqueId, pricebookId);
	}

	public void updateWasPrices(WasPriceFixture... wasPrices) {
		for (WasPriceFixture wasPrice: wasPrices) {
			Assertions.assertThat(wasPrice.getUniqueId()).isNotNull();
			Assertions.assertThat(wasPrice.getWasPrice()).isNotNull();
			jdbcTemplate.update(UPDATE_PRICEBOOKWASPRICE_WASPRICE, wasPrice.getWasPrice(), wasPrice.getUniqueId());
		}
	}

	public Double getPricebookCostBasePrice(int uniqueId, int pricebookId) {
		return jdbcTemplate.queryForObject(SELECT_PRICEBOOKCOST_BASEPRICE, Double.class, uniqueId, pricebookId);
	}

	public int getHistoricalUniqueId(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_HISTORICAL_UNIQUEID, int.class, participationId);
	}


	public double getItemizedHistoryPrice(int participationId, int uniqueId) {
		return jdbcTemplate.queryForObject(SELECT_HISTORICAL_ITEMIZED_PRICE, double.class, participationId, uniqueId);
	}

	public double getCalculatedHistoryChangeValue(int tParticipationId) {
		return jdbcTemplate.queryForObject(SELECT_HISTORICAL_CALCULATED_CHANGE_VALUE, double.class, tParticipationId);
	}

	/**
	 * Load the products for a participation and return list of the unique ids that are owned.
	 */
	public List<Integer> getOwnedUniqueIds(int participationId) {
		return getParticipationProducts(participationId).stream()
				.filter(ParticipationProduct::getIsOwner)
				.map(ParticipationProduct::getUniqueId)
				.collect(Collectors.toList());
	}

	/**
	 * Check applicable tables for any references to the given participation id
	 * with assertions.
	 */
	public void assertParticipationNotPresent(ParticipationItemFixture fixture) {
		int participationId = fixture.getParticipationId();
		String fixtureAsString = fixture.toString();
		Assertions.assertThat(getParticipationItemPartial(participationId)).as("Unexpected participationItemPartial record: " + fixtureAsString).isNull();
		Assertions.assertThat(getParticipationCalculatedDiscountCount(participationId)).as("Unexpected participationCalculatedDiscount record: " + fixtureAsString).isEqualTo(0);
		Assertions.assertThat(getParticipationItemizedDiscountCount(participationId)).as("Unexpected " +
				"participationItemizedDiscount record: " + fixtureAsString).isEqualTo(0);
		Assertions.assertThat(getParticipationProductCount(participationId)).as("Unexpected participationProduct record: " + fixtureAsString).isEqualTo(0);
		Assertions.assertThat(getParticipationSaleIdCount(participationId)).as("Unexpected participation id in product.sale record: " + fixtureAsString).isEqualTo(0);
		Assertions.assertThat(getPricebookCostParticipationCount(participationId)).as("Unexpected participation id in pricebook_cost record: " + fixtureAsString).isEqualTo(0);
	}

	/**
	 * Check applicable tables for any references to the given participation id and return true/false.
	 */
	public boolean isParticipationPresent(int participationId) {
		return getParticipationItemPartial(participationId) != null
				|| getParticipationCalculatedDiscountCount(participationId) != 0
				|| getParticipationProductCount(participationId) != 0
				|| getParticipationSaleIdCount(participationId) != 0
				|| getPricebookCostParticipationCount(participationId) != 0;
	}

	/**
	 * Insert a record with the given SQL query and params, and return the generated
	 * key created as part of the insert.
	 *
	 * @param sql The insert to execute
	 * @param args The arguments to be bound to the query.
	 * @return The generated key for the newly inserted record.
	 */
	public Number insert(final String sql, final Object... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			ArgumentPreparedStatementSetter setter = new ArgumentPreparedStatementSetter(args);
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setter.setValues(stmt);
			return stmt;
		}, keyHolder);
		return keyHolder.getKey();
	}
}
