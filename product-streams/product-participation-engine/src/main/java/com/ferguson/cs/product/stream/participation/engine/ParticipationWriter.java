package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

public class ParticipationWriter {
	private final ParticipationService participationService;
	private final ConstructService constructService;

	public ParticipationWriter(ParticipationService participationService, ConstructService constructService) {
		this.participationService = participationService;
		this.constructService = constructService;
	}

	@Transactional
	public void processPublish(ParticipationItem item, Date processingDate) {
		if (BooleanUtils.isTrue(participationService.getParticipationIsActive(item.getId()))) {
			participationService.deactivateParticipation(
					ParticipationItemPartial.builder()
							.participationId(item.getId())
							.lastModifiedUserId(item.getLastModifiedUserId())
							.build(),
					processingDate);
		}
		participationService.publishParticipation(item, processingDate);
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_UPDATE,
				processingDate
		);
	}

	@Transactional
	public void processActivation(ParticipationItemPartial itemPartial, Date processingDate) {
		participationService.activateParticipation(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_CLEANUP,
				processingDate
		);
	}

	@Transactional
	public void processDeactivation(ParticipationItemPartial itemPartial, Date processingDate) {
		if (itemPartial.getIsActive()) {
			participationService.deactivateParticipation(itemPartial, processingDate);
		}
		participationService.unpublishParticipation(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.ARCHIVED,
				null,
				processingDate
		);
	}

	@Transactional
	public void processUnpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		if (participationService.getParticipationIsActive(itemPartial.getParticipationId())) {
			participationService.deactivateParticipation(itemPartial, processingDate);
		}
		participationService.unpublishParticipation(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.DRAFT,
				null,
				processingDate
		);
	}
}
