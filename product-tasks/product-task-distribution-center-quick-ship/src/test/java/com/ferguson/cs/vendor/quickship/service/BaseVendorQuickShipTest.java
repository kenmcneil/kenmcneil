package com.ferguson.cs.vendor.quickship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;
import com.ferguson.cs.test.BaseTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@EnableTaskIntegrationTesting
public abstract class BaseVendorQuickShipTest extends BaseTest {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

}
