package com.ferguson.cs.product.task.dy;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.task.test.BaseBatchStepTest;
import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;

@EnableTaskIntegrationTesting
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(locations = { "classpath:application-unit.yml" })
@Transactional
public class DynamicYieldFeedTaskIT extends BaseBatchStepTest {

	/*
	 * This will write a csv file for the Dynamic Yield product
	 * feed to a temp directory
	 */
	@Test
	@Ignore("Use only for dev testing")
	public void testWriteProductData() throws NoSuchJobException {
		JobLauncherTestUtils util = getJobLauncherTestUtils("dynamicYieldExportJob");
		util.launchStep("writeDyItems");
	}

	/*
	 * This will write a csv file for the Dynamic Yield product
	 * feed to a temp directory and then ftp it to Dynamic Yield
	 * for processing
	 */
	@Test
	@Ignore("Use only for dev testing")
	public void testDyFeedJob() throws NoSuchJobException {
		JobLauncherTestUtils util = getJobLauncherTestUtils("dynamicYieldExportJob");
		try {
			util.launchJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}