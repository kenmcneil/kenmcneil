package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.ParticipationServiceImpl;
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
public class ParticipationItemizedV1Lifecycle extends ParticipationLifecycleBase {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);
	public static final String NAME_WITH_MAJOR_VERSION = "participation-itemized@1";

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationDao participationDao;

	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		ParticipationItemPartial itemPartial = ParticipationItemPartial.builder()
				.participationId(item.getId())
				.saleId(null)
				.startDate(item.getSchedule() == null ? null : item.getSchedule().getFrom())
				.endDate(item.getSchedule() == null ? null : item.getSchedule().getTo())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.isActive(false)
				.build();

		int rowsAffected = participationDao.upsertParticipationItemPartial(itemPartial);

//		rowsAffected += participationDao.upsertParticipationItemizedDiscounts(
//				item.getId(), getParticipationItemizedDiscounts(item));

		LOG.debug("{}: {} total rows updated to publish", item.getId(), rowsAffected);

		return rowsAffected;
	}

	@Override
	public int activate(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int deactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}

	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		return 0;
	}
}
