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
	 * This will test downloading csv files from Supply's FTP account. There is no
	 * dev account so any files downloaded will be production files.
	 * 
	 * Coordinate running this test with Supply if you're going to be retrieving and
	 * and processing files production files from the FTP directory. Otherwise use
	 * your own test files.
	 */
	@Test
	//@Ignore("Use only for dev testing")
	public void testWriteProductData() throws NoSuchJobException {
		JobLauncherTestUtils util = getJobLauncherTestUtils("dynamicYieldExportJob");
		util.launchStep("writeDyItems");
	}

	/**
	 * 
	 * This job will download all csv files from Supply's FTP directory and import
	 * them into the configured temporary directories. The data will be processed
	 * and load them into the category tables and then archive the files to the
	 * configured archive directory.
	 * 
	 * There is currently not a dev ftp directory so any files downloaded from FTP
	 * will be production files.
	 * 
	 * Coordinate running this test with Supply if you're going to be retrieving and
	 * and processing files from the FTP directory. Otherwise use your own test
	 * files.
	 */
	/*@Test
	@Ignore("Use only for dev testing")
	public void testSupplyCategoryJob() throws NoSuchJobException {
		JobLauncherTestUtils util = getJobLauncherTestUtils("supplyCategoryFileImportJob");
		try {
			util.launchJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}