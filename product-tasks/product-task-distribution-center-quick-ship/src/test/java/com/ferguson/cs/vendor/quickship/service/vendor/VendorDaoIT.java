package com.ferguson.cs.vendor.quickship.service.vendor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;
import com.ferguson.cs.vendor.quickship.service.BaseVendorQuickShipTest;

public class VendorDaoIT extends BaseVendorQuickShipTest {

	@Autowired
	private VendorDao vendorDao;

	private static final String insertVendorSql =
			"INSERT INTO omc.dbo.vendor (vendorid, email, communicationMethod)" +
					" VALUES (?, ?, 'email')";

	private Integer insertTestVendor(String vendorId, String email) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertVendorSql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, vendorId);
			ps.setString(2, email);
			return ps;
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Test
	@Ignore("Refactor to use seeded data")
	public void testGetFergusonQuickShipDistributionCenterList() {
		QuickShipDistributionCenterSearchCriteria criteria = new QuickShipDistributionCenterSearchCriteria();
		criteria.setProductId("0303");
		criteria.setManufacturerName("Ginger");
		criteria.setFinishDescription("Satin Nickel");

		List<DistributionCenter> distributionCenterList = vendorDao.getFergusonQuickShipDistributionCenterList(criteria);
		assertNotNull(distributionCenterList);
		assertTrue(distributionCenterList.size() > 0);
	}

	@Test
	public void testInsertDistributionCenterProductQuickShip() {
		DistributionCenter distributionCenter = new DistributionCenter();
		distributionCenter.setId(insertTestVendor("TEST_DC", "test_dc@test_dc.com"));

		Product product = new Product();
		product.setId(3693);

		DistributionCenterProductQuickShip distributionCenterProductQuickShip = new DistributionCenterProductQuickShip();
		distributionCenterProductQuickShip.setDistributionCenter(distributionCenter);
		distributionCenterProductQuickShip.setProduct(product);

		vendorDao.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);

		DistributionCenterProductQuickShip retrievedDistributionCenterProductQuickShip =
				vendorDao.getDistributionCenterProductQuickShip(distributionCenterProductQuickShip.getId());

		assertTrue(retrievedDistributionCenterProductQuickShip.getId().equals(distributionCenterProductQuickShip.getId()));
		assertTrue(retrievedDistributionCenterProductQuickShip != null
				&& retrievedDistributionCenterProductQuickShip.getDistributionCenter() != null
				&& retrievedDistributionCenterProductQuickShip.getDistributionCenter().getId().equals(
						distributionCenterProductQuickShip.getDistributionCenter().getId()));
		assertTrue(retrievedDistributionCenterProductQuickShip != null
				&& retrievedDistributionCenterProductQuickShip.getProduct() != null
				&& retrievedDistributionCenterProductQuickShip.getProduct().getId().equals(
						distributionCenterProductQuickShip.getProduct().getId()));
	}

	@Test
	public void testTruncateVendorProductQuickShipTable() {
		DistributionCenter distributionCenter = new DistributionCenter();
		distributionCenter.setId(insertTestVendor("TEST_DC", "test_dc@test_dc.com"));

		Product product = new Product();
		product.setId(3693);

		DistributionCenterProductQuickShip distributionCenterProductQuickShip = new DistributionCenterProductQuickShip();
		distributionCenterProductQuickShip.setDistributionCenter(distributionCenter);
		distributionCenterProductQuickShip.setProduct(product);

		vendorDao.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);

		DistributionCenterProductQuickShip retrievedDistributionCenterProductQuickShip =
				vendorDao.getDistributionCenterProductQuickShip(distributionCenterProductQuickShip.getId());

		assertTrue(retrievedDistributionCenterProductQuickShip.getId().equals(distributionCenterProductQuickShip.getId()));
		assertTrue(retrievedDistributionCenterProductQuickShip != null
				&& retrievedDistributionCenterProductQuickShip.getDistributionCenter() != null
				&& retrievedDistributionCenterProductQuickShip.getDistributionCenter().getId().equals(
						distributionCenterProductQuickShip.getDistributionCenter().getId()));
		assertTrue(retrievedDistributionCenterProductQuickShip != null
				&& retrievedDistributionCenterProductQuickShip.getProduct() != null
				&& retrievedDistributionCenterProductQuickShip.getProduct().getId().equals(
						distributionCenterProductQuickShip.getProduct().getId()));

		vendorDao.truncateVendorProductQuickShipTable();

		retrievedDistributionCenterProductQuickShip =
				vendorDao.getDistributionCenterProductQuickShip(distributionCenterProductQuickShip.getId());
		assertNull(retrievedDistributionCenterProductQuickShip);
	}

}
