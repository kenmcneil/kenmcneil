package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

import lombok.RequiredArgsConstructor;

/**
 * This class is responsible for all database changes related to itemized discounts.
 *
 * Example content record:
 *
 *   "content": {
 *     "_type": "participation-itemized@1.0.0",
 *     "itemizedDiscounts": {
 *       "_type": "atom-section@1.0.0",
 *       "discounts": {
 *         "list": [],
 *         "_type": "atom-tuple-list@1.0.0"
 *       }
 *     }
 *   }
 *
 * Null checking is mostly omitted since a content object is validated before publishing.
 */
@RequiredArgsConstructor
public class ParticipationItemizedV1Lifecycle implements ParticipationLifecycle {
	public static final String CONTENT_TYPE = "participation-itemized@1";

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationDao participationDao;

	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		return 0;
	}

	@Override
	public int activate(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int activateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int deactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}
}
