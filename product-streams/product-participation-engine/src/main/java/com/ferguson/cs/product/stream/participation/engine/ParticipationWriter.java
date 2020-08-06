package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

public class ParticipationWriter {
	private final ParticipationLifecycleService participationLifecycleService;
	private final ConstructService constructService;

	public ParticipationWriter(
			ParticipationLifecycleService participationLifecycleService,
			ConstructService constructService
	) {
		this.participationLifecycleService = participationLifecycleService;
		this.constructService = constructService;
	}

	/**
	 * Publish the given ParticipationItem by upserting its data into SQL tables.
	 * If this Participation is currently active then deactivates it first.
	 */
	@Transactional
	public void processPublish(ParticipationItem item, Date processingDate) {
		if (BooleanUtils.isTrue(participationLifecycleService.getParticipationIsActive(item.getId()))) {
			participationLifecycleService.deactivateByType(
					ParticipationItemPartial.builder()
							.participationId(item.getId())
							.lastModifiedUserId(item.getLastModifiedUserId())
							.build(),
					processingDate);
		}

		participationLifecycleService.publishByType(item, processingDate);
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_UPDATE,
				processingDate,
				item.getLastModifiedUserId()
		);
	}

	@Transactional
	public void processActivation(ParticipationItemPartial itemPartial, Date processingDate) {
		participationLifecycleService.activateByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_CLEANUP,
				processingDate,
				itemPartial.getLastModifiedUserId()
		);
	}

	@Transactional
	public void processDeactivation(ParticipationItemPartial itemPartial, Date processingDate) {
		if (itemPartial.getIsActive()) {
			participationLifecycleService.deactivateByType(itemPartial, processingDate);
		}
		participationLifecycleService.unpublishByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.ARCHIVED,
				null,
				processingDate,
				itemPartial.getLastModifiedUserId()
		);
	}

	/**
	 * Deactivate given participation if needed and unpublish. If the record is not present in SQL then simply
	 * set Construct record to draft status.
	 */
	@Transactional
	public void processUnpublish(ParticipationItem item, Date processingDate) {
		// Get the participation item from SQL. If not there, then we'll skip any SQL changes and change the Construct
		// record to be DRAFT status (assumes the SQL Participation record is not present because the engine already
		// unpublished the record in SQL and the corresponding unpublish Construct update failed silently).
		ParticipationItemPartial itemPartial = participationLifecycleService.getParticipationItemPartial(item.getId());
		if (itemPartial != null) {
			// Update the user id in SQL to the latest from Construct.
			itemPartial.setLastModifiedUserId(item.getLastModifiedUserId());

			// If active then deactivate before unpublishing.
			if (itemPartial.getIsActive()) {
				participationLifecycleService.deactivateByType(itemPartial, processingDate);
			}

			participationLifecycleService.unpublishByType(itemPartial, processingDate);
		}

		// Update record in Construct to DRAFT status.
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.DRAFT,
				null,
				processingDate,
				item.getLastModifiedUserId()
		);
	}
}
