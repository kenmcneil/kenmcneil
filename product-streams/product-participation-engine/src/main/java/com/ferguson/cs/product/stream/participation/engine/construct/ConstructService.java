package com.ferguson.cs.product.stream.participation.engine.construct;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

public interface ConstructService {
	ParticipationItem getNextPendingActivationParticipation();
	ParticipationItem getNextPendingDeactivationParticipation();
	ParticipationItem getNextPendingUnpublishParticipation();
}
