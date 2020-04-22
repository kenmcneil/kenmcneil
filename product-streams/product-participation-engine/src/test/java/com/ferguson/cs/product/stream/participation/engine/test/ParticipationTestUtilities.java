package com.ferguson.cs.product.stream.participation.engine.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSchedule;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationCalculatedDiscountsFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

@Service
public class ParticipationTestUtilities {

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

	public static List<Integer> participationIdsList;
	public static List<Integer> saleIdsList;
	public static List<List<Integer>> uniqueIdsList;
	public static List<Set<Integer>> deletedUidsList;
	public static List<ParticipationItemUpdateStatus> updateStatusList;
	public static final Integer USERID = 999;
	public static final int PARTICIPATION_ID_NEEDS_UPDATE = 10;
	public static final int PARTICIPATION_ID_NEEDS_UNPUBLISH = 20;
	public static final int PARTICIPATION_ID_NEEDS_CLEANUP = 30;
	public static final int PARTICIPATION_ID_TO_EXPIRE = 40;
	public static final int PARTICIPATION_ID_WITH_CONTENT_NEED_UPDATE = 50;
	public static int totalProductUniqueIdsUsed;
	public static int totalProductUniqueIdsForUpdate;
	public static int totalProductUniqueIdsForDelete;
	public static int totalActiveParticipationToProcessed;
	public static int totalActiveParticipationToUpddate;
	public static int totalActiveParticipationToDelete;

	// for Service/Dao Layers
	public static final int TEST_USERID = 1234;
	public static final int TEST_USERID_2 = 2345;
	public static final int SEED_SALEID = -820;
	public static final int WINNING_SALEID = -810;
	public static final int SEED_UNIQUEID = 820000;
	public static final int SEED_PARTICIPATION_ID = -82;
	public static final int WINNING_PARTICIPATION_ID = -81;
	public static final long SEED_JOBID = -82828282;
	public static final long WINNING_JOBID = -81818181;
	public static final int PB22_ID = 22;
	public static final int PB1_ID = 1;
	public static final double PB22_20PERCENT_DISCOUNT_OFFSET = 0.80;
	public static final double PB1_10PERCENT_DISCOUNT_OFFSET = 0.90;
	public static final int PB22_AMOUNT_DISCOUNT_20 = -20;
	public static final int PB1_AMOUNT_DISCOUNT_10 = -10;
	public static final double PB22_SEED_BASEPRICE = 100.00;
	public static final double PB1_SEED_BASEPRICE = 110.00;

	public static final String SELECT_PARTICIPATIONTRACKING_BY_PARTICIPATIONID =
			"SELECT id, uniqueId, saleId, startDate, participationid, userId, " +
			"IIF(updatingJobId = 0, 0, 1) AS needsUpdate, " +
			"IIF(deletingJobId = 0, 0, 1) AS needsDelete " +
			"FROM mmc.product.participationTracking " +
			"WHERE participationId = ?";
	public static final String SELECT_SALE_ID =
			"SELECT saleId FROM mmc.product.sale WHERE uniqueId = ?";
	public static final String SELECT_PRODUCT_MODIFIED =
			"SELECT modifiedDate FROM mmc.product.modified WHERE uniqueId = ?";
	public static final String SELECT_PARTICIPATION_ID =
			"SELECT participationId FROM mmc.product.sale WHERE uniqueId = ?";
	public static final String INSERT_PARTICIPATION_TRACKING =
			"INSERT INTO mmc.product.participationTracking" +
			"(uniqueId, saleId, startDate, participationId, " +
			"userId, updatingJobId) " +
			"VALUES (?, ?, ?, ?, " + TEST_USERID + ", ?)";
	public static final String INSERT_DELETABLE_PARTICIPATION_TRACKING =
			"INSERT INTO mmc.product.participationTracking" +
			"(uniqueId, saleId, startDate, participationId, " +
			"userId, deletingJobId) " +
			"VALUES (?, ?, GETDATE(), ?, + " + TEST_USERID + ", ?)";
	private static final String MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_NEGATIVE_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationProduct " +
					"WHERE participationId < 0";
	private static final String MANUAL_DELETE_TRACKING_BY_TEST_UNIQUEIDS =
			"DELETE FROM mmc.product.participationTracking " +
					"WHERE uniqueId IN (" + SEED_UNIQUEID + ", " + SEED_UNIQUEID + 100 + ")";
	public static final String MANUAL_DELETE_TRACKINGS_WITH_JOBID =
			"DELETE FROM mmc.product.participationTracking " +
					"WHERE updatingJobId < 0 " +
					"OR deletingJobId < 0";
	private static final String MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_TESTIDS =
			"DELETE FROM mmc.product.participationProduct " +
					"WHERE uniqueId IN (" + SEED_UNIQUEID + ", " + SEED_UNIQUEID + 100 + ")";
	private static final String MANUAL_DELETE_PARTIAL_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationItemPartial " +
					"WHERE participationId < 0";
	private static final String MANUAL_OFFSALE_UPDATE =
			"UPDATE mmc.product.sale SET saleId = 0 " +
			"WHERE saleId < 0";
	public static final String MANUAL_MODIFIED_UPDATE =
			"UPDATE mmc.product.modified SET modifiedDate = ? WHERE uniqueId = ?";
	public static final String SELECT_PARTICIPTATION_ACTIVE_BY_PARTICIPATIONID =
			"SELECT * FROM mmc.product.participationItemPartial " +
			"WHERE participationId = ?";
	public static final String SELECT_PARTICIPTATIONPARTIAL_ID_BY_PARTICIPATIONID =
			"SELECT id FROM mmc.product.participationItemPartial " +
			"WHERE participationId = ?";
	public static final String SELECT_PARTICIPATION_CALCULATED_DISCOUNT_COUNT_BY_PARTICIPATIONID =
			"SELECT COUNT(*) FROM mmc.product.participationCalculatedDiscount " +
			"WHERE participationId = ?";
	public static final String SELECT_PARTICIPTATIONPRODUCT_BY_PARTICIPATIONID =
			"SELECT uniqueId FROM mmc.product.participationProduct " +
			"WHERE participationId = ?";
	public static final String SELECT_TRACKING_PARTICIPATIONID_BY_DELETINGJOBID =
			"SELECT participationId FROM mmc.product.participationTracking " +
			"WHERE deletingJobId = ?";
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
	public static final String UPDATE_PARTICIPATION_TRACKING_TO_DELETABLE = "UPDATE mmc.product.participationTracking " +
			"SET deletingJobId = ? WHERE uniqueId = ? AND saleId = ? AND participationId = ?";
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
	private static final String MANUAL_DELETE_CALCULATED_DISCOUNT_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.product.participationCalculatedDiscount " +
					"WHERE participationId < 0";
	private static final String MANUAL_DELETE_PRICEBOOK_COST_BY_TEST_PARTICIPATIONID =
			"DELETE FROM mmc.dbo.PriceBook_Cost " +
					"WHERE participationId < 0";
	public static final String SELECT_LASTONSALE_BASEPRICE_BY_UNIQUEID_PRICEBOOKID =
			"SELECT basePrice FROM mmc.product.participationLastOnSale WHERE uniqueId = ? AND pricebookId = ?";
	public static final String UPDATE_LATEST_BASEPRICE_BY_UNIQUEID_PRICEBOOKID =
			"UPDATE mmc.product.latestBasePrice SET basePrice = ? WHERE uniqueId = ? AND pricebookId = ?";

//	static {
//		//static data - list of same size to create participationItem
//		participationIdsList = new ArrayList<>(Arrays.asList(PARTICIPATION_ID_NEEDS_UPDATE, PARTICIPATION_ID_NEEDS_UNPUBLISH,
//				PARTICIPATION_ID_NEEDS_CLEANUP, PARTICIPATION_ID_TO_EXPIRE, PARTICIPATION_ID_WITH_CONTENT_NEED_UPDATE));
//		saleIdsList = new ArrayList<>(Arrays.asList(100, 200, 300, 400, 500));
//
//		uniqueIdsList = new ArrayList<>();
//		uniqueIdsList.add(new ArrayList<>(Arrays.asList(1000, 2000, 3000, 4000, 5000)));                        // 5 uniqueIds for NEEDS_UPDATE
//		uniqueIdsList.add(new ArrayList<>(Arrays.asList(4000, 5000, 6000, 7000, 8000, 9000, 10000, 11111)));    // 8 uniqueIds
//		uniqueIdsList.add(new ArrayList<>(Arrays.asList(1111, 2222, 3333, 4444)));                              // 4 uniqueIds
//		uniqueIdsList.add(new ArrayList<>(Arrays.asList(7000, 8000, 9000, 99999)));                             // 4 uniqueIds
//		uniqueIdsList.add(new ArrayList<>(Arrays.asList(1100, 1200, 1300, 1400)));								// 4 uniqueIds
//
//		deletedUidsList = new ArrayList<>();
//		deletedUidsList.add(new HashSet<>(Arrays.asList(5555, 7777, 8888)));                    // 3 uniqueIds
//		deletedUidsList.add(new HashSet<>(singletonList(6666)));                                // 1
//		deletedUidsList.add(new HashSet<>());
//		deletedUidsList.add(new HashSet<>());
//		deletedUidsList.add(new HashSet<>(singletonList(1000)));								// 1
//
//		updateStatusList = new ArrayList<>();
//		updateStatusList.add(ParticipationItemUpdateStatus.NEEDS_UPDATE);
//		updateStatusList.add(ParticipationItemUpdateStatus.NEEDS_UNPUBLISH);
//		updateStatusList.add(ParticipationItemUpdateStatus.NEEDS_CLEANUP);
//		updateStatusList.add(ParticipationItemUpdateStatus.NEEDS_UPDATE); // to test isExpire flag regardless of updateStatus
//		updateStatusList.add(ParticipationItemUpdateStatus.NEEDS_UPDATE);
//
//		//Add up all the uniqueIds from uniqueIdsList & deletedUidsList and store the sum in totalProductUniqueIdsUsed
//		uniqueIdsList.forEach(idList -> totalProductUniqueIdsUsed += idList.size());
//		deletedUidsList.forEach((delList -> totalProductUniqueIdsUsed += delList.size()));
//		totalProductUniqueIdsForUpdate = 9;     // size of the uniqueIds list in the participationItem with id = PARTICIPATION_ID_NEEDS_UPDATE
//		totalProductUniqueIdsForDelete = totalProductUniqueIdsUsed - totalProductUniqueIdsForUpdate;    // sum of all uniqueIds and deleteUniqueId in ParticipationItems except the "NEEDS_UPDATE" status
//		totalActiveParticipationToProcessed = participationIdsList.size();
//		totalActiveParticipationToDelete = 3;// # of Participation with status NEEDS_UNPUBLISH, NEEDS_CLEANUP & Expired
//		totalActiveParticipationToUpddate = 2; // # of Participation with status NEEDS_UPDATE
//	}

	@Autowired
	public JdbcTemplate jdbcTemplate;

	public Integer firstTemplateId;

	public LocalDate today = LocalDate.now();
	public LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);
	public LocalDate lastWeek = today.minus(1, ChronoUnit.WEEKS);
	public LocalDate nextWeek = today.plus(7, ChronoUnit.DAYS);

	public Date rightNow = toDate(today);
	public Date recentStartDate = toDate(yesterday);
	public Date lessRecentStartDate = toDate(lastWeek);
	public Date sevenDaysHence = toDate(nextWeek);

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
				item.getUserId() == null ? TEST_USERID : item.getUserId(),
				item.getIsActive());

		// Insert any participationProduct records.
		if (!CollectionUtils.isEmpty(item.getUniqueIds())) {
			for (Integer uId : item.getUniqueIds()){
				jdbcTemplate.update(INSERT_PARTICIPATION_PRODUCT, item.getParticipationId(), uId, false);
			}
		}

		if (firstTemplateId == null) {
			firstTemplateId = jdbcTemplate.queryForObject(SELECT_FIRST_CALCULATED_DISCOUNT_TEMPLATE, Integer.class);
		}

		// Insert any participationCalculatedDiscount records.
		if (!CollectionUtils.isEmpty(item.getCalculatedDiscounts())) {
			for (ParticipationCalculatedDiscountsFixture discount: item.getCalculatedDiscounts()) {
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

	protected ParticipationItemFixture readParticipationTestValues(int participationId) {
		ParticipationItemFixture values = new ParticipationItemFixture();
		values.setParticipationId(participationId);
		return values;
	}

	public ParticipationItem createParticipationItemWithContentMap(
			boolean createContent, Integer id,
			Integer saleId,
			Integer userId,
			Date startDate, Date endDate,
			List<Integer> uniqueIds,
			Set<Integer> deletedUids,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus
	) {
		ParticipationItem item = new ParticipationItem();
		item.setId(id);

		if (createContent) {
			item.setContent(makeParticipationContent_v1(saleId, uniqueIds));
		} else {
			item.setSaleId(saleId);
			item.setProductUniqueIds(uniqueIds);
		}

		item.setLastModifiedUserId(userId);
		item.setProductUniqueIds(uniqueIds);
		item.setDeletedProductUniqueIds(deletedUids);
		item.setStatus(status);
		item.setUpdateStatus(updateStatus);

		ParticipationItemSchedule schedule = new ParticipationItemSchedule();
		schedule.setFrom(startDate);
		schedule.setTo(endDate);
		item.setSchedule(schedule);

		return item;
	}

	public Map<String, Object> makeParticipationContent_v1(int saleId, List<Integer> productUniqueIds) {
		Map<String, Object> contentMap = new HashMap<>();
		contentMap.put("_type", "participation@1.0.0");

		//Path to saleId "productSale.saleId"
		Map<String, Object> saleIdMap = new HashMap<>();
		saleIdMap.put("_type", "atom-product-sale@1.0.0");
		saleIdMap.put("saleId", Integer.valueOf(saleId));
		contentMap.put("productSale", saleIdMap);

		//Path to uniqueIds list: "calculatedDiscounts.uniqueIds.list"";
		Map<String, Object> productUniqueIdsMap = new HashMap<>();
		productUniqueIdsMap.put("_type", "atom-list@1.0.0");
		productUniqueIdsMap.put("list", productUniqueIds);

		Map<String, Object> discountMap = new HashMap<>();
		discountMap.put("_type", "atom-list@1.0.0");
		discountMap.put("uniqueIds", productUniqueIdsMap);
		contentMap.put("calculatedDiscounts", discountMap);

		return contentMap;
	}

//	public Queue<ParticipationItem> getParticipationItemPageSearchResultQueue() {
//		if (participationItemPageSearchResultQueue.isEmpty()) {
////			populateParticipationItemPageSearchResultQueue();
//		}
//		return participationItemPageSearchResultQueue;
//	}
//
//	public List<ParticipationItem> getParticipationItems() {
//		return participationItems;
//	}
//
//	public Stack<ParticipationItemUpdateStatus> getParticipationUpdateStatusStack() {
//		return updateParticipationStatusStack;
//	}

//	public ParticipationIdToUpdateStatusMap getParticipationIdToUpdateStatusMap() {
//		if (participationIdToUpdateStatusMap.getParticipationIdToUpdateStatusMap().isEmpty()) {
//			createParticipationIdToUpdateStatusMap();
//		}
//
//		return participationIdToUpdateStatusMap;
//	}
//
//	/**
//	 * Create the ParticipationIdToUpdateStatusMap instance using the participationItem from the participationItems List
//	 */
//	private void createParticipationIdToUpdateStatusMap() {
//		if (participationItems == null || participationItems.isEmpty()) {
//			participationItems = createParticipationItems();
//		}
//		for (ParticipationItem item : participationItems) {
//			participationIdToUpdateStatusMap.addIfAbsent(item.getId(), item.getUpdateStatus());
//
//			//let add the updateStatus into the stack
//			updateParticipationStatusStack.push(item.getUpdateStatus());
//		}
//	}

//	private List<ParticipationItem> createParticipationItems() {
//		LocalDate today = LocalDate.now();
//		LocalDate twoDaysAgo = today.minus(2, ChronoUnit.DAYS);
//		LocalDate nextWeek = today.plus(1, ChronoUnit.WEEKS);
//		Date startDate;
//		Date endDate;
//		List<ParticipationItem> items = new ArrayList<>();
//		for (int i = 0; i < participationIdsList.size(); i++) {
//			Integer participationId = participationIdsList.get(i);
//			if (participationId == PARTICIPATION_ID_NEEDS_CLEANUP || participationId == PARTICIPATION_ID_TO_EXPIRE) {
//				startDate = toDate(twoDaysAgo);
//				endDate = toDate(today);
//			} else {
//				startDate = toDate(today);
//				endDate = toDate(nextWeek);
//			}
//			items.add(createParticipationItemWithContentMap( participationId == PARTICIPATION_ID_WITH_CONTENT_NEED_UPDATE,
//					participationId,
//					saleIdsList.get(i),
//					USERID,
//					startDate, endDate,
//					uniqueIdsList.get(i),
//					deletedUidsList.get(i),
//					ParticipationItemStatus.PUBLISHED,
//					updateStatusList.get(i)));
//		}
//		return items;
//	}

//	/**
//	 * Populate ParticpationItem Queue with predefined static data in the right order as the criteria stack on the Reader
//	 * so that we can return the mock data corresponding to the call to findMatchingParticipationItems
//	 */
//	private void populateParticipationItemPageSearchResultQueue() {
//
//		if (participationItems == null || participationItems.isEmpty()) {
//			participationItems = createParticipationItems();
//		}
//		PagedSearchResults searchResults;
//		List<ParticipationItem> expireList = new ArrayList<>();
//		List<ParticipationItem> needUpdateList = new ArrayList<>();
//		List<ParticipationItem> needUnpublished = new ArrayList<>();
//
//		for (ParticipationItem item : participationItems ) {
//			switch (item.getId()) {
//				case PARTICIPATION_ID_NEEDS_UPDATE:
//				case PARTICIPATION_ID_WITH_CONTENT_NEED_UPDATE:
//					needUpdateList.add(item);
//					break;
//				case PARTICIPATION_ID_NEEDS_UNPUBLISH:
//					needUnpublished.add(item);
//					break;
//				case PARTICIPATION_ID_NEEDS_CLEANUP:
//				case PARTICIPATION_ID_TO_EXPIRE:
//					expireList.add(item);
//					break;
//
//			}
//		}
//
//		//adding this search result into the queue in the same order as the criteria stack
//		searchResults = new PagedSearchResults(new Paging(1, needUpdateList.size(),needUpdateList.size(),1), needUpdateList);
//		participationItemPageSearchResultQueue.add(searchResults);
//
//		searchResults = new PagedSearchResults(new Paging(1, needUnpublished.size(), needUnpublished.size(),1), needUnpublished);
//		participationItemPageSearchResultQueue.add(searchResults);
//
//		// create the last PageSearchResult for Need Cleanup and expired item
//		searchResults = new PagedSearchResults(new Paging(1,expireList.size(),expireList.size(),1), expireList);
//		participationItemPageSearchResultQueue.add(searchResults);
//	}

	/**
	 * clean up any old seed data erroneously left in the db
	 */
	public void deleteSeedParticipationTracking(JdbcTemplate jdbcTemplate) {
		jdbcTemplate.update(MANUAL_DELETE_PRICEBOOK_COST_BY_TEST_PARTICIPATIONID);
		jdbcTemplate.update(MANUAL_DELETE_CALCULATED_DISCOUNT_BY_TEST_PARTICIPATIONID);
		jdbcTemplate.update(MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_NEGATIVE_PARTICIPATIONID);
		jdbcTemplate.update(MANUAL_DELETE_TRACKING_BY_TEST_UNIQUEIDS);
		jdbcTemplate.update(MANUAL_DELETE_TRACKINGS_WITH_JOBID);
		jdbcTemplate.update(MANUAL_OFFSALE_UPDATE);
		jdbcTemplate.update(MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_TESTIDS);
		jdbcTemplate.update(MANUAL_DELETE_PARTICIPATIONPRODUCT_BY_NEGATIVE_PARTICIPATIONID);
		jdbcTemplate.update(MANUAL_DELETE_PARTIAL_BY_TEST_PARTICIPATIONID);
	}

	public ParticipationItem createSingleParticipationItemWithSpecifiedUniqueIds(int numOfUniqueIds) {

		ParticipationItem seedParticipationItem = new ParticipationItem();
		seedParticipationItem.setId(SEED_PARTICIPATION_ID);
		seedParticipationItem.setSaleId(SEED_SALEID);
		List<Integer> seedUniqueIds = new ArrayList<>();
		for (int i=0; i < numOfUniqueIds; i++) {
			seedUniqueIds.add(SEED_UNIQUEID + (i * 100));
		}
		seedParticipationItem.setProductUniqueIds(seedUniqueIds);
		seedParticipationItem.setStatus(ParticipationItemStatus.PUBLISHED);
		seedParticipationItem.setUpdateStatus(ParticipationItemUpdateStatus.NEEDS_UPDATE);
		seedParticipationItem.setLastModifiedUserId(TEST_USERID);
		ParticipationItemSchedule schedule = new ParticipationItemSchedule();
		schedule.setFrom(recentStartDate);
		schedule.setTo(sevenDaysHence);
		seedParticipationItem.setSchedule(schedule);

		return seedParticipationItem;
	}

	public static void createPublishedParticipationDataInSQL(JdbcTemplate jdbcTemplate, ParticipationItem item) {
		createPublishedParticipationDataInSQL(jdbcTemplate, item, false, false);
	}

	public static void createPublishedParticipationDataInSQL(JdbcTemplate jdbcTemplate, ParticipationItem item,
															 boolean includeCaclDiscounts, boolean isPercent) {
		// insert into to partial table
		jdbcTemplate.update(INSERT_PARTICIPATION_ITEM_PARTIAL_SQL,
				item.getId(),
				item.getSaleId(),
				item.getSchedule().getFrom(),
				item.getSchedule().getTo(),
				item.getLastModifiedUserId());

		//insert into participationProduct table
		for (Integer uId : item.getProductUniqueIds()){
			jdbcTemplate.update(INSERT_PARTICIPATION_PRODUCT, item.getId(), uId);
		}

		//insert participationCalculatedDiscount
		if(includeCaclDiscounts) {

			//insert to PriceBook_cost table for the pricebook & uniqueId referencing here
			//To create a dynamic pricing test data: item 1 will have the cost & baseprice of SEED_BASEPRICE_0, 2nd item will be double, 3rd item will be tripple, etc.
			int i = 0;
			for (Integer uniqueId : item.getProductUniqueIds()) {
				i++;
				double pb22_basePrice_cost = PB22_SEED_BASEPRICE * i;
				double pb1_basePrice_cost = PB1_SEED_BASEPRICE * i;
				jdbcTemplate.update(UPSERT_PRICEBOOK_COST,
						pb22_basePrice_cost, //update statement
						pb22_basePrice_cost,
						item.getLastModifiedUserId(),
						item.getId(),
						uniqueId,
						PB22_ID,
						uniqueId, //start insert statement if not found
						PB22_ID,
						pb22_basePrice_cost,
						pb22_basePrice_cost,
						item.getLastModifiedUserId(),
						item.getId()
						);

				jdbcTemplate.update(UPSERT_PRICEBOOK_COST,
						pb1_basePrice_cost,  //update statement
						pb1_basePrice_cost,
						item.getLastModifiedUserId(),
						item.getId(),
						uniqueId,
						PB1_ID,
						uniqueId,  //start insert statement if not found
						PB1_ID,
						pb1_basePrice_cost,
						pb1_basePrice_cost,
						item.getLastModifiedUserId(),
						item.getId()
						);
			}

			//Retrieve calculatedDiscountTemplateId from the table
			int templateId = jdbcTemplate.queryForObject(
					SELECT_FIRST_CALCULATED_DISCOUNT_TEMPLATE, Integer.class);
			//add pb22 % or amount Calculated Discount
			jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
					item.getId(),
					PB22_ID,
					isPercent ? PB22_20PERCENT_DISCOUNT_OFFSET : PB22_AMOUNT_DISCOUNT_20,
					isPercent,
					templateId);

			//add pb1 % or amount Calculated Discount
			jdbcTemplate.update(INSERT_PARTICIPATION_CALCULATED_DISCOUNT,
					item.getId(),
					PB1_ID,
					isPercent ? PB1_10PERCENT_DISCOUNT_OFFSET : PB1_AMOUNT_DISCOUNT_10,
					isPercent,
					templateId);
		}
	}
}
