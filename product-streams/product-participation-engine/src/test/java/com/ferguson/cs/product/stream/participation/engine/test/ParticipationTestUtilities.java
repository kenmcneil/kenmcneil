package com.ferguson.cs.product.stream.participation.engine.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationCalculatedDiscountsFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationProductFixture;

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
					"(id, templateTypeId, template, description, isActive) " +
					"VALUES (?, ?, ?, ?, ?)";

	public static final String INSERT_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_TYPE =
			"INSERT INTO mmc.product.participationCalculatedDiscountTemplateType     " +
					"(id, title) " +
					"VALUES (?, ?, ?, ?)";

	public static final String SELECT_FIRST_CALCULATED_DISCOUNT_TEMPLATE =
			"SELECT TOP(1) id FROM mmc.product.participationCalculatedDiscountTemplate";

	public static final String SELECT_SALE_ID =
			"SELECT saleId FROM mmc.product.sale WHERE uniqueId = ?";
	public static final String SELECT_PARTICIPATION_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationItemPartial " +
					"WHERE participationId = ?";
	public static final String SELECT_PARTICIPTATIONPARTIAL_ID_BY_PARTICIPATIONID =
			"SELECT id FROM mmc.product.participationItemPartial " +
					"WHERE participationId = ?";
	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";
	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId = ?";
	public static final String SELECT_PARTICIPATIONPRODUCT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationProduct " +
					"WHERE participationId = ?";
	public static final String SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID =
			"SELECT count(*) FROM mmc.product.sale WHERE participationId = ?";
	public static final String SELECT_PARTICIPATIONPRODUCT_BY_PARTICIPATIONID =
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
	private static final ResultSetExtractor<ParticipationItemFixture> SINGLETON_PARTICIPATION_ITEM_FIXTURE_EXTRACTOR =
			singletonExtractor(new BeanPropertyRowMapper<>(ParticipationItemFixture.class));
	private static final ResultSetExtractor<Integer> SINGLETON_INTEGER_OR_NULL_EXTRACTOR =
			singletonExtractor((rs, i) -> rs.getInt(0));
	@Autowired
	public JdbcTemplate jdbcTemplate;
	public Integer firstTemplateId;

	// modified from https://stackoverflow.com/a/16390624/9488171
	private static <T> ResultSetExtractor<T> singletonExtractor(RowMapper<? extends T> mapper) {
		return rs -> rs.next() ? mapper.mapRow(rs, 1) : null;
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public void insertParticipation(ParticipationItemFixture item) {
		// Insert participationItemPartial record.
		// Default to test user id if none specified.
		jdbcTemplate.update(INSERT_PARTICIPATION_ITEM_PARTIAL_SQL,
				item.getParticipationId(),
				item.getSaleId(),
				item.getStartDate(),
				item.getEndDate(),
				item.getLastModifiedUserId() == null ? TEST_USERID : item.getLastModifiedUserId(),
				item.getIsActive());

		// Insert any participationProduct records.
		if (!CollectionUtils.isEmpty(item.getUniqueIds())) {
			for (Integer uId : item.getUniqueIds()) {
				jdbcTemplate.update(INSERT_PARTICIPATION_PRODUCT, item.getParticipationId(), uId, false);
			}
		}

		if (firstTemplateId == null) {
			firstTemplateId = jdbcTemplate.queryForObject(SELECT_FIRST_CALCULATED_DISCOUNT_TEMPLATE, Integer.class);
		}

		// Insert any participationCalculatedDiscount records.
		if (!CollectionUtils.isEmpty(item.getCalculatedDiscounts())) {
			for (ParticipationCalculatedDiscountsFixture discount : item.getCalculatedDiscounts()) {
				jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
						item.getParticipationId(),
						discount.getPricebookId(),
						discount.getChangeValue(),
						discount.getIsPercent(),
						discount.getTemplateId() == null ? firstTemplateId : discount.getTemplateId()
				);
			}
		}
	}

	/**
	 * For existence check.
	 */
	public Integer getParticipationItemPartialId(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPTATIONPARTIAL_ID_BY_PARTICIPATIONID,
				SINGLETON_INTEGER_OR_NULL_EXTRACTOR, participationId);
	}

	public Integer getParticipationCalculatedDiscountCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID,
				new Object[]{participationId}, Integer.class);
	}

	public Integer getParticipationProductCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PARTICIPATIONPRODUCT_COUNT_BY_PARTICIPATIONID,
				new Object[]{participationId}, Integer.class);
	}

	public Integer getParticipationSaleIdCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_SALE_ID_COUNT_BY_PARTICIPATIONID,
				new Object[]{participationId}, Integer.class);
	}

	public Integer getPricebookCostParticipationCount(int participationId) {
		return jdbcTemplate.queryForObject(SELECT_PRICEBOOK_COST_COUNT_BY_PARTICIPATIONID,
				new Object[]{participationId}, Integer.class);
	}

	public List<ParticipationProductFixture> getParticipationProductAsFixtures(int participationId) {
		return jdbcTemplate.queryForList(SELECT_PARTICIPATIONPRODUCT_BY_PARTICIPATIONID,
				new Object[]{participationId},
				ParticipationProductFixture.class);
	}

	public List<ParticipationCalculatedDiscountsFixture> getParticipationCalculatedDiscountsAsFixtures(int participationId) {
		return jdbcTemplate.queryForList(SELECT_PARTICIPATION_CALCULATED_DISCOUNT_BY_PARTICIPATIONID,
				new Object[]{participationId},
				ParticipationCalculatedDiscountsFixture.class);
	}

	/**
	 * Returns participationItemPartial row if record is found else null.
	 */
	public ParticipationItemFixture getParticipationItemPartialAsFixture(int participationId) {
		return jdbcTemplate.query(SELECT_PARTICIPATION_BY_PARTICIPATIONID,
				SINGLETON_PARTICIPATION_ITEM_FIXTURE_EXTRACTOR, participationId);
	}

	/**
	 * Retrieve all the normalized data for a participation, as a ParticipationItemFixture
	 * with the participation item details, products, and calculated discounts.
	 */
	public ParticipationItemFixture getEntireParticipationAsFixture(int participationId) {
		ParticipationItemFixture fixture = getParticipationItemPartialAsFixture(participationId);
		if (fixture != null) {
			fixture.setProducts(getParticipationProductAsFixtures(participationId));
			fixture.setCalculatedDiscounts(getParticipationCalculatedDiscountsAsFixtures(participationId));
		}
		return fixture;
	}

	/**
	 * Check applicable tables for any references to the given participation id.
	 */
	public boolean isParticipationPresent(int participationId) {
		return getParticipationItemPartialId(participationId) != null
				|| getParticipationCalculatedDiscountCount(participationId) != 0
				|| getParticipationProductCount(participationId) != 0
				|| getParticipationSaleIdCount(participationId) != 0
				|| getPricebookCostParticipationCount(participationId) != 0;
	}
}
