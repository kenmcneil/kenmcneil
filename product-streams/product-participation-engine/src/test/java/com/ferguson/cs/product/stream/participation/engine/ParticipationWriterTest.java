package com.ferguson.cs.product.stream.participation.engine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.test.BaseTest;

public class ParticipationWriterTest extends BaseTest {
	private ConstructService constructService;
	private ParticipationLifecycleService participationLifecycleService;
	private ParticipationWriter participationWriter;

	@Before
	public void beforeTest() {
		constructService = mock(ConstructService.class);
		participationLifecycleService = mock(ParticipationLifecycleService.class);
		participationWriter = new ParticipationWriter(constructService, participationLifecycleService);
	}

	@Test
	public void processUnpublish_unpublishedFromSQLAlready() {
		int testUserId = 2524;
		Date processingDate = new Date();
		ParticipationItem item = ParticipationItem.builder().id(100000).lastModifiedUserId(testUserId).build();

		doReturn(null).when(participationLifecycleService).getParticipationItemPartial((anyInt()));

		participationWriter.processUnpublish(item, processingDate);

		verify(participationLifecycleService, times(1)).getParticipationItemPartial(anyInt());
		verify(constructService, times(1)).updateParticipationItemStatus(
				eq(item.getId()), eq(ParticipationItemStatus.DRAFT), isNull(), any(Date.class), eq(testUserId));
	}

	@Test
	public void processUnpublish_normalUnpublish_notActivated() {
		int testUserId = 2524;
		Date processingDate = new Date();
		ParticipationItem item = ParticipationItem.builder().id(100000).lastModifiedUserId(testUserId).build();
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(8200)
				.startDate(null)
				.endDate(null)
				.lastModifiedUserId(item.getLastModifiedUserId())
				.contentTypeId(3)
				.isActive(false)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.build();
		doReturn(itemPartial).when(participationLifecycleService).getParticipationItemPartial((anyInt()));

		participationWriter.processUnpublish(item, processingDate);

		verify(participationLifecycleService, times(1)).getParticipationItemPartial(anyInt());
		verify(participationLifecycleService, times(1)).unpublishByType(
				any(ParticipationItemPartial.class), any(Date.class));
		verify(participationLifecycleService, never()).deactivateByType(
				any(ParticipationItemPartial.class), any(Date.class));
		verify(constructService, times(1)).updateParticipationItemStatus(
				eq(item.getId()), eq(ParticipationItemStatus.DRAFT), isNull(), any(Date.class), eq(testUserId));
	}

	@Test
	public void processUnpublish_normalUnpublish_activated() {
		int testUserId = 2524;
		Date processingDate = new Date();
		ParticipationItem item = ParticipationItem.builder().id(100000).lastModifiedUserId(testUserId).build();
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(8200)
				.startDate(null)
				.endDate(null)
				.lastModifiedUserId(item.getLastModifiedUserId())
				.contentTypeId(3)
				.isActive(true)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.build();
		doReturn(itemPartial).when(participationLifecycleService).getParticipationItemPartial((anyInt()));

		participationWriter.processUnpublish(item, processingDate);

		verify(participationLifecycleService, times(1)).getParticipationItemPartial(anyInt());
		verify(participationLifecycleService, times(1)).unpublishByType(
				any(ParticipationItemPartial.class), any(Date.class));
		verify(participationLifecycleService, times(1)).deactivateByType(
				any(ParticipationItemPartial.class), any(Date.class));
		verify(constructService, times(1)).updateParticipationItemStatus(
				eq(item.getId()), eq(ParticipationItemStatus.DRAFT), isNull(), any(Date.class), eq(testUserId));
	}
}
