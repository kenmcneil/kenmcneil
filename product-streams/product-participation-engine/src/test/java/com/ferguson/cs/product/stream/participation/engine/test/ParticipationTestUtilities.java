package com.ferguson.cs.product.stream.participation.engine.test;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationProduct;

@Service
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
			"INSERT INTO mmc.product.participationProduct " +
					"(participationId, uniqueId, isOwner) " +
					"VALUES (?, ?, ?)";

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

	public static final String SELECT_SALE_ID =
			"SELECT saleId FROM mmc.product.sale WHERE uniqueId = ?";

	public static final String SELECT_PARTICIPATION_PARTIAL_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationItemPartial " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationProduct " +
					"WHERE participationId = ?";

	public static final String SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.product.sale WHERE participationId = ?";

	public static final String SELECT_PARTICIPATION_PRODUCT_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationProduct " +
					"WHERE participationId = ?";

	public static final String SELECT_PRICEBOOK_COST_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.dbo.pricebook_cost WHERE participationId = ?";

	public static final String UPSERT_PRICEBOOK_COST =
			"UPDATE mmc.dbo.PriceBook_Cost " +
					"SET basePrice = ?, " +
					"cost = ?, " +
					"userId = ?, " +
					"participationId = ? " +
					"WHERE UniqueId = ? " +
					"AND PriceBookId = ? " +
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
			"SELECT Cost FROM mmc.dbo.PriceBook_Cost " +
					"WHERE UniqueId = ? AND PriceBookId = ?";

	public static final String SELECT_PRICEBOOK_COST_BY_UNIQUEID_PRICEBOOKID_PARTICIPATIONID_USERID =
			"SELECT Cost FROM mmc.dbo.PriceBook_Cost WHERE UniqueId = ? AND pricebookId = ? AND participationId = ? AND userId = ?";

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

	@Autowired
	public JdbcTemplate jdbcTemplate;

	/**
	 * Returns the id of a calculated discount template record. If not populated yet, insert
	 * a fixture record for it and for a type. Will return the same id on subsequent calls.
	 */
	public int insertCalculatedDiscountTemplateAndType() {
		int templateTypeId = insert(INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_TYPE,
				"TEST_TEMPLATE_TYPE").intValue();
		return insert(INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE,
				templateTypeId, "TEST GET ${percent}% OFF!", "TEST GET __% OFF!", true).intValue();
	}

	/**
	 * Inserts all the data from the fixture into the appropriate tables.
	 * This is similar to a publish operation.
	 *
	 * Defaults lastModifiedUserId to test user id if none specified.
	 * Defaults isActive to false if not specified.
	 */
	public void insertParticipationFixture(ParticipationItemFixture fixture) {
		// Insert participationItemPartial record.
		jdbcTemplate.update(INSERT_PARTICIPATION_ITEM_PARTIAL_SQL,
				fixture.getParticipationId(),
				fixture.getSaleId(),
				fixture.getStartDate(),
				fixture.getEndDate(),
				fixture.getLastModifiedUserId() == null ? TEST_USERID : fixture.getLastModifiedUserId(),
				fixture.getIsActive() == null ? false : fixture.getIsActive()
		);

		// Insert any uniqueIds as participationProduct records with isOwner = false.
		if (!CollectionUtils.isEmpty(fixture.getUniqueIds())) {
			for (Integer uId : fixture.getUniqueIds()) {
				jdbcTemplate.update(INSERT_PARTICIPATION_PRODUCT, fixture.getParticipationId(), uId, false);
			}
		}

		// Insert any participationCalculatedDiscount records.
		if (!CollectionUtils.isEmpty(fixture.getCalculatedDiscounts())) {
			for (ParticipationCalculatedDiscount discount : fixture.getCalculatedDiscounts()) {
				jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
						fixture.getParticipationId(),
						discount.getPricebookId(),
						discount.getChangeValue(),
						discount.getIsPercent(),
						discount.getTemplateId()
				);
			}
		}
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

	/**
	 * Check applicable tables for any references to the given participation id.
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
