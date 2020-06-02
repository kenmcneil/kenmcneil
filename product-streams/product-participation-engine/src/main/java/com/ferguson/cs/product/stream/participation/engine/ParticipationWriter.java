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
	 * Poll for a ParticipationItem to publish, and upserts its data into SQL tables.
	 * If this Participation is currently active then deactivate it first.
	 */
//	@Transactional
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
				processingDate
		);
	}

//	@Transactional
	public void processActivation(ParticipationItemPartial itemPartial, Date processingDate) {



		//HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
		participationLifecycleService.activateByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_CLEANUP,
				processingDate
		);
	}

//	@Transactional
	public void processDeactivation(ParticipationItemPartial itemPartial, Date processingDate) {
		if (itemPartial.getIsActive()) {
			participationLifecycleService.deactivateByType(itemPartial, processingDate);
		}
		participationLifecycleService.unpublishByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.ARCHIVED,
				null,
				processingDate
		);
	}

//	@Transactional
	public void processUnpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		if (participationLifecycleService.getParticipationIsActive(itemPartial.getParticipationId())) {
			participationLifecycleService.deactivateByType(itemPartial, processingDate);
		}
		participationLifecycleService.unpublishByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.DRAFT,
				null,
				processingDate
		);
	}
}
