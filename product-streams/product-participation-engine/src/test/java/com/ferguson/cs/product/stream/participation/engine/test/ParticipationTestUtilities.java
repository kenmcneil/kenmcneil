package com.ferguson.cs.product.stream.participation.engine.test;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
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
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationProduct;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.PricebookCost;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductModified;
import com.ferguson.cs.product.stream.participation.engine.test.model.ProductSaleParticipation;

public class ParticipationTestUtilities {
	public static final int TEST_USERID = 1234;
	public static final int TEST_USERID_2 = 2345;
	public static final int SEED_SALEID = -820;
	public static final int WINNING_SALEID = -810;
	public static final int SEED_UNIQUEID = 820000;
	public static final int SEED_PARTICIPATION_ID = -82;
	public static final int WINNING_PARTICIPATION_ID = -81;
	public static final int PB22_ID = 22;
	public static final int PB1_ID = 1;
	public static final double PB22_20PERCENT_DISCOUNT_OFFSET = 0.80;
	public static final double PB1_10PERCENT_DISCOUNT_OFFSET = 0.90;
	public static final int PB22_AMOUNT_DISCOUNT_20 = -20;
	public static final int PB1_AMOUNT_DISCOUNT_10 = -10;
	public static final double PB22_SEED_BASEPRICE = 100.00;
	public static final double PB1_SEED_BASEPRICE = 110.00;

	public static final String INSERT_PARTICIPATION_ITEM_PARTIAL_SQL =
			"INSERT INTO mmc.product.participationItemPartial " +
					"(participationId, saleId, startDate, endDate, lastModifiedUserId, isActive) " +
					"VALUES (?, ?, ?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_PRODUCT =
			"INSERT INTO mmc.product.participationProduct VALUES " +
					"(:participationId, :uniqueId, :isOwner)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT =
			"INSERT INTO mmc.product.participationCalculatedDiscount " +
					"(participationId, priceBookId, changeValue, isPercent, templateId) " +
					"VALUES (?, ?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE =
			"INSERT INTO mmc.product.participationCalculatedDiscountTemplate " +
					"(templateTypeId, template, description, active) " +
					"VALUES (?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_TYPE =
			"INSERT INTO mmc.product.participationCalculatedDiscountTemplateType     " +
					"(templateType) " +
					"VALUES (?)";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationProduct " +
					"WHERE participationId = ?";

	public static final String SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.product.sale WHERE participationId = ?";

	public static final String SELECT_PRICEBOOK_COST_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.dbo.pricebook_cost WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE =
			"SELECT TOP 1 id FROM mmc.product.participationCalculatedDiscountTemplate";

	public static final String SELECT_PARTICIPATION_PARTIAL_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationItemPartial " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_BY_PARTICIPATIONID =
			"SELECT participationId, uniqueId, isOwner FROM mmc.product.participationProduct " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID =
			"SELECT participationId, priceBookId, changeValue, isPercent, templateId " +
					"FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";

	public static final String SELECT_PRODUCT_SALE_LINK_BY_UNIQUE_ID =
			"SELECT * FROM mmc.product.sale WHERE uniqueId IN ( :uniqueIds )";

	public static final String SELECT_PRODUCT_MODIFIED_BY_UNIQUE_ID =
			"SELECT * FROM mmc.product.modified WHERE uniqueId IN ( :uniqueIds )";

	public static final String UPSERT_PRICEBOOK_COST =
			"UPDATE mmc.dbo.PriceBook_Cost " +
					"SET basePrice = ?, cost = ?, userId = ?, participationId = ? " +
					"WHERE UniqueId = ? AND PriceBookId = ? " +
					"IF @@ROWCOUNT = 0 " +
					"INSERT INTO mmc.dbo.PriceBook_Cost " +
					"(uniqueId, priceBookId, basePrice, cost, userId, participationId) " +
					"VALUES (?, ?, ?, ?, ?, ?)";

	public static final String UPDATE_PRICEBOOK_COST_COST =
			"UPDATE mmc.dbo.PriceBook_Cost SET cost = ? WHERE UniqueId = ? AND PriceBookId = ?";

	public static final String SELECT_PRICEBOOK_COST_BY_PARTICIPATIONID_UNIQUEID_PRICEBOOKID_USERID =
			"SELECT Cost FROM mmc.dbo.PriceBook_Cost " +
					"WHERE participationId = ? AND UniqueId = ? AND PriceBookId = ? AND userId = ? ";

	public static final String SELECT_PRICEBOOK_COST_BY_UNIQUEID_PRICEBOOKID =
			"SELECT cost, basePrice, userId, participationId " +
					"FROM mmc.dbo.PriceBook_Cost " +
					"WHERE uniqueId IN ( :uniqueIds ) AND pricebookId IN ( :pricebookIds )";

	public static final String INSERT_PARTICIPATION_LASTONSALE =
			"INSERT INTO mmc.product.participationLastOnSale (pricebookId, uniqueId, saleDate, basePrice) " +
					"VALUES (?, ?, ?, ?)";

	public static final String SELECT_LASTONSALE_BASEPRICE_BY_UNIQUEID_PRICEBOOKID =
			"SELECT basePrice FROM mmc.product.participationLastOnSale WHERE uniqueId = ? AND pricebookId = ?";

	public static final String UPDATE_LATEST_BASEPRICE_BY_UNIQUEID_PRICEBOOKID =
			"UPDATE mmc.product.latestBasePrice SET basePrice = ? WHERE uniqueId = ? AND pricebookId = ?";

	private static final String MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_NEGATIVE_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationProduct " +
					"WHERE participationId < 0";

	private static final String MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_TESTIDS =
			"DELETE FROM mmc.product.participationProduct " +
					"WHERE uniqueId IN (" + SEED_UNIQUEID + ", " + SEED_UNIQUEID + 100 + ")";

	private static final String MANUAL_DELETE_PARTIAL_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationItemPartial " +
					"WHERE participationId < 0";

	private static final String MANUAL_OFFSALE_UPDATE =
			"UPDATE mmc.product.sale SET saleId = 0 " +
					"WHERE saleId < 0";

	private static final String MANUAL_DELETE_CALCULATED_DISCOUNT_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId < 0";

	private static final String MANUAL_DELETE_PRICEBOOK_COST_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.dbo.PriceBook_Cost " +
					"WHERE participationId < 0";

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
	private final ParticipationEngineSettings participationEngineSettings;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private int nextTestParticipationId;

	public ParticipationTestUtilities(JdbcTemplate jdbcTemplate, ParticipationEngineSettings participationEngineSettings) {
		this.jdbcTemplate = jdbcTemplate;
		this.participationEngineSettings = participationEngineSettings;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
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
	 * Set initial participation id to given value.
	 */
	public void setInitialParticipationId(int id) {
		nextTestParticipationId = id;
	}

	/**
	 * Returns a new test participation fixture id. Participation ids are not auto ids in the
	 * participationItemPartial table.
	 */
	public int getNextTestParticipationId() {
		return nextTestParticipationId++;
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
	 * Defaults isActive to false if not specified.
	 */
	public void insertParticipationFixture(ParticipationItemFixture fixture) {
		validateAndSetDefaults(fixture);

		int participationId = fixture.getParticipationId();

		// Insert participationItemPartial record.
		jdbcTemplate.update(INSERT_PARTICIPATION_ITEM_PARTIAL_SQL,
				participationId,
				fixture.getSaleId(),
				fixture.getStartDate(),
				fixture.getEndDate(),
				fixture.getLastModifiedUserId(),
				fixture.getIsActive()
		);

		// Insert any uniqueIds as participationProduct records with isOwner = false.
		if (!CollectionUtils.isEmpty(fixture.getUniqueIds())) {
			SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(
					fixture.getUniqueIds().stream()
							.map(uniqueId -> new ParticipationProduct(participationId, uniqueId, false))
							.collect(Collectors.toList())
			);
			namedParameterJdbcTemplate.batchUpdate(INSERT_PARTICIPATION_PRODUCT, batch);
		}

		// Insert any participationCalculatedDiscount records.
		nullSafeStream(fixture.getCalculatedDiscountFixtures())
				.map(discountFixture -> discountFixture.toParticipationCalculatedDiscount(participationId))
				.forEach(discount -> jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
						discount.getParticipationId(),
						discount.getPricebookId(),
						discount.getChangeValue(),
						discount.getIsPercent(),
						discount.getTemplateId())
				);
	}

	public Integer getParticipationCalculatedDiscountCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID,
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

	public List<ProductSaleParticipation> getProductSaleParticipations(List<Integer> uniqueIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueIds", uniqueIds);
		return namedParameterJdbcTemplate.query(SELECT_PRODUCT_SALE_LINK_BY_UNIQUE_ID,
				namedParameters, BeanPropertyRowMapper.newInstance(ProductSaleParticipation.class));
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

	public List<PricebookCost> getPricebookCosts(List<Integer> uniqueIds, List<Integer> pricebookIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueIds", uniqueIds)
				.addValue("pricebookIds", pricebookIds);
		return namedParameterJdbcTemplate.query(SELECT_PRICEBOOK_COST_BY_UNIQUEID_PRICEBOOKID,
				namedParameters, BeanPropertyRowMapper.newInstance(PricebookCost.class));
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
