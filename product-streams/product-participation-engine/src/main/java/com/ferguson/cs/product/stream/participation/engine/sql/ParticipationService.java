package com.ferguson.cs.product.stream.participation.engine.sql;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

public interface ParticipationService {

	void activateParticipation(ParticipationItem item, Date processingDate);

	void deactivateParticipation(ParticipationItem item, Date processingDate);

	void unpublishParticipation(ParticipationItem item, Date processingDate);
}
