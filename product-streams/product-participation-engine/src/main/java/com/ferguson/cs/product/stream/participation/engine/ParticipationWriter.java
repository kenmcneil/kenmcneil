package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
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
	public void processUnpublish(ParticipationItem item, Date processingDate) {
		if (participationService.getParticipationIsActive(item.getId())) {
			participationService.deactivateParticipation(item, processingDate);
		}
		participationService.unpublishParticipation(item, processingDate);
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.DRAFT,
				null,
				processingDate
		);
	}

	@Transactional
	public void processActivation(ParticipationItem item, Date processingDate) {
		participationService.activateParticipation(item, processingDate);
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.PUBLISHED,
				ParticipationItemUpdateStatus.NEEDS_CLEANUP,
				processingDate
		);
	}

	@Transactional
	public void processDeactivation(ParticipationItem item, Date processingDate) {
		participationService.deactivateParticipation(item, processingDate);
		participationService.unpublishParticipation(item, processingDate);
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.ARCHIVED,
				null,
				processingDate
		);
	}
}
