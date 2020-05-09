package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.ContentEvent;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class ConstructServiceImpl implements ConstructService {

	private final ParticipationEngineSettings participationEngineSettings;
	private final ContentEventRepository contentEventRepository;
	private final ParticipationItemRepository participationItemRepository;

	public ConstructServiceImpl(
			ParticipationEngineSettings participationEngineSettings,
			ContentEventRepository contentEventRepository,
			ParticipationItemRepository participationItemRepository
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.contentEventRepository = contentEventRepository;
		this.participationItemRepository = participationItemRepository;
	}

	@Override
	public ParticipationItem getNextPendingPublishParticipation(Integer minParticipationId) {
		return participationItemRepository.getNextPendingPublishParticipation(minParticipationId);
	}

	@Override
	public ParticipationItem getNextPendingUnpublishParticipation(Integer minParticipationId) {
		return participationItemRepository.getNextPendingUnpublishParticipation(minParticipationId);
	}

	@Override
	public void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			Date processingDate
	) {
		ArgumentAssert.notNull(status, "status");
		ArgumentAssert.notNull(processingDate, "processingDate");

		// update the participation record
		participationItemRepository.updateParticipationItemStatus(
				participationId, status, updateStatus,
				participationEngineSettings.getTaskUserId(), processingDate);

		// add event for this update with a partial participation record for the details
		ParticipationItem eventItem = ParticipationItem.builder()
				.id(participationId)
				.lastModifiedUserId(participationEngineSettings.getTaskUserId())
				.lastModifiedDate(processingDate)
				.status(status)
				.updateStatus(updateStatus)
				.build();
		ContentEvent contentEvent = new ContentEvent();
		contentEvent.setParticipationItem(eventItem);
		contentEvent.setLastModifiedDate(processingDate);
		contentEvent.setLastModifiedUserId(participationEngineSettings.getTaskUserId());
		contentEventRepository.save(contentEvent);
	}
}
