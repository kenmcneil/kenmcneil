package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ContentErrorMessage;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ValidationException;

/**
 * Given a ParticipationItem, calls the correct lifecycle strategy bean based on the
 * content type, which is the schema name and version of the content type definition
 * used to create the record's content.
 *
 * When adding a new Participation type to the engine, add its bean to the
 * ParticipationLifecycleService constructor in ParticipationEngineConfiguration.
 *
 */
public class ParticipationLifecycleServiceImpl implements ParticipationLifecycleService {
	private final static Logger LOG = LoggerFactory.getLogger(ParticipationLifecycleServiceImpl.class);

	private static final String CONTENT_TYPE_KEY = "_type";
	private static final String defaultContentTypeNameWithMajorVersion = "participation@1";

	private final Map<String, ParticipationLifecycle> lifecyclesByContentType;

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationDao participationDao;

	public ParticipationLifecycleServiceImpl(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationDao participationDao,
			ParticipationLifecycle... lifecycles
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.participationDao = participationDao;
		lifecyclesByContentType = Arrays.stream(lifecycles)
				.collect(Collectors.toMap(ParticipationLifecycle::getContentType, lifecycle -> lifecycle));
	}

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate) {
		return participationDao.getNextParticipationPendingActivation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate) {
		return participationDao.getNextExpiredParticipation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	/**
	 * Call the lifecycle publish method for the type of the given Participation to handle
	 * adding its effect-specific records.
	 */
	public int publishByType(ParticipationItem item, Date processingDate) {
		int rowsAffected = getLifecycleFor(getContentTypeNameWithMajorVersion(item)).publish(item, processingDate);
		LOG.debug("{}: {} total rows updated to publish", item.getId(), rowsAffected);
		return rowsAffected;
	}

	/**
	 * Call activate method for the type of the given Participation; call deactivateEffects
	 * on all lifecycle types to remove effects on entities owned in other Participations
	 * that are becoming newly-owned by the activating Participation (such as products);
	 * and call activateEffects for the activating Participation.
	 */
	public int activateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		ParticipationLifecycle activatingLifecycle = getLifecycleFor(itemPartial.getContentType());

		LOG.debug("==== activating participation {} ====", participationId);

		int affectedRows = participationDao.setParticipationIsActive(participationId, true);

		// (1) Apply effect-specific queries for activating this Participation. Perform set up
		// to call deactivateEffects() and activateEffects().
		affectedRows += activatingLifecycle.activate(itemPartial, processingDate);

		// (2) Make deactivation changes for all Participation types, to remove their effects on
		// entities becoming owned by the activating Participation.
		affectedRows += lifecyclesByContentType.values().stream()
				.map(lifecycle -> lifecycle.deactivateEffects(itemPartial, processingDate))
				.reduce(0, Integer::sum);

		// (3) Apply effects of the activating itemPartial record to newly-owned entities.
		affectedRows += activatingLifecycle.activateEffects(itemPartial, processingDate);

		LOG.debug("{}: {} total rows updated to activate", participationId, affectedRows);

		return affectedRows;
	}

	/**
	 * Call deactivate methods for each Participation type: call deactivate for this item;
	 * call deactivate effects for this item; and finally call activateEffects for all types.
	 */
	public int deactivateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		ParticipationLifecycle deactivatingLifecycle = getLifecycleFor(itemPartial.getContentType());

		LOG.debug("==== deactivating participation {} ====", participationId);

		int affectedRows = participationDao.setParticipationIsActive(participationId, false);

		// (1) Run effect-specific queries for deactivating this Participation. Perform set up
		// for calling activateEffects() and deactivateEffects().
		affectedRows += deactivatingLifecycle.deactivate(itemPartial, processingDate);

		// (2) Remove effects of the deactivating record from un-owned entities.
		affectedRows += deactivatingLifecycle.deactivateEffects(itemPartial, processingDate);

		// (3) Apply effects for all Participation types, for entities being dis-owned by the
		// deactivating Participation that are becoming owned by other active Participations.
		affectedRows += lifecyclesByContentType.values().stream()
				.map(lifecycle -> lifecycle.activateEffects(itemPartial, processingDate))
				.reduce(0, Integer::sum);

		LOG.debug("{}: {} total rows updated to deactivate", participationId, affectedRows);

		return affectedRows;
	}

	/**
	 * Call the lifecycle unpublish method for the type of the given Participation to handle
	 * deleting effect-specific records it added when it published the Participation.
	 */
	public int unpublishByType(ParticipationItemPartial itemPartial, Date processingDate) {
		int rowsAffected = getLifecycleFor(itemPartial.getContentType()).unpublish(itemPartial, processingDate);
		LOG.debug("{}: {} total rows deleted to unpublish participation", itemPartial.getParticipationId(), rowsAffected);
		return rowsAffected;
	}

	@Override
	public Boolean getParticipationIsActive(Integer participationId) {
		return participationDao.getParticipationIsActive(participationId);
	}

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	@Transactional
	@Override
	public int syncToCurrentPriorityParticipation() {
		return participationDao.syncToCurrentPriorityParticipation();
	}

	/**
	 * Get the content._type return with the minor and patch version removed, in the form
	 * "content-type@majorVersion", e.g. "participation@1". Return null if not present.
	 */
	private String getContentTypeNameWithMajorVersion(ParticipationItem item) {
		if (item.getContent() != null && item.getContent().containsKey(CONTENT_TYPE_KEY)) {
			String fullType = (String) item.getContent().get(CONTENT_TYPE_KEY);
			if (fullType != null) {
				String[] parts = fullType.split("\\.");
				return parts[0];
			}
		}
		return null;
	}

	/**
	 * Get the content._type value or null if not present.
	 */
	private String getContentType(ParticipationItem item) {
		if (item.getContent() != null && item.getContent().containsKey(CONTENT_TYPE_KEY)) {
			return (String) item.getContent().get(CONTENT_TYPE_KEY);
		}
		return null;
	}

	/**
	 * Get a lifecycle instance for the type of the given Participation. For compatibility
	 * with the records already in SQL (currently published records), a null contentType
	 * defaults to "participation@1".
	 */
	private ParticipationLifecycle getLifecycleFor(String contentTypeNameWithMajorVersion) {
		String defaultedType = contentTypeNameWithMajorVersion == null
				? defaultContentTypeNameWithMajorVersion
				: contentTypeNameWithMajorVersion;

		if (lifecyclesByContentType.containsKey(defaultedType)) {
			return lifecyclesByContentType.get(defaultedType);
		}

		throw new ValidationException(ContentErrorMessage.INVALID_PARTICIPATION_CONTENT_TYPE.toString());
	}
}
