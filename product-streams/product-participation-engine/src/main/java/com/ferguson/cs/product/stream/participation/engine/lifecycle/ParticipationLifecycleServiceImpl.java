package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.model.ContentErrorMessage;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
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
 */
public class ParticipationLifecycleServiceImpl implements ParticipationLifecycleService {
	private static final Logger LOG = LoggerFactory.getLogger(ParticipationLifecycleServiceImpl.class);

	private static final String CONTENT_TYPE_KEY = "_type";

	private final Map<ParticipationContentType, ParticipationLifecycle> lifecyclesByContentType;

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationCoreDao participationCoreDao;

	public ParticipationLifecycleServiceImpl(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationLifecycle... lifecycles
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.participationCoreDao = participationCoreDao;
		lifecyclesByContentType = Arrays.stream(lifecycles)
				.collect(Collectors.toMap(ParticipationLifecycle::getContentType, lifecycle -> lifecycle));
	}

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate) {
		return participationCoreDao.getNextParticipationPendingActivation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate) {
		return participationCoreDao.getNextExpiredParticipation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	/**
	 * Call the lifecycle publish method for the type of the given Participation to handle
	 * adding its effect-specific records.
	 */
	@Override
	public int publishByType(ParticipationItem item, Date processingDate) {
		LOG.debug("==== publishing participation {} ====", item.getId());

		// Get content enum type based on _type in the content. Must be a valid content type.
		ParticipationContentType contentType = ParticipationContentType
				.fromNameWithMajorVersion(getContentTypeNameWithMajorVersion(item));
		if (contentType == null) {
			throw new ValidationException(ContentErrorMessage.INVALID_PARTICIPATION_CONTENT_TYPE.toString());
		}

		// Use the lifecycle class for the Participation to publish the record and a snapshot for history.
		int itemLifecyleId = contentType.contentTypeId();
		int rowsAffected = getLifecycle(itemLifecyleId).publish(item, processingDate);
		LOG.debug("{}: {} total rows updated to publish", item.getId(), rowsAffected);
		getLifecycle(itemLifecyleId).publishToHistory(item, processingDate);

		return rowsAffected;
	}

	/**
	 * Call activate method for the type of the given Participation; call deactivateEffects
	 * on all lifecycle types to remove effects on entities owned in other Participations
	 * that are becoming newly-owned by the activating Participation (such as products);
	 * and call activateEffects for the activating Participation.
	 */
	@Override
	public int activateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		ParticipationLifecycle activatingLifecycle = getLifecycle(itemPartial.getContentTypeId());

		LOG.debug("==== activating participation {} ====", participationId);

		int totalRows = participationCoreDao.setParticipationIsActive(participationId, true);

		// (1) Apply non-effect-specific queries for activating this Participation. Perform set up
		// to call deactivateEffects() and activateEffects().
		totalRows += activatingLifecycle.activate(itemPartial, processingDate);

		int rowsAffected = participationCoreDao.updateLastOnSaleForDeactivatingProducts(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale base prices saved", participationId, rowsAffected);

		// (2) Make deactivation changes for all Participation types, to remove their effects on
		// entities becoming owned by the activating Participation.
		totalRows += lifecyclesByContentType.values().stream()
				.map(lifecycle -> lifecycle.deactivateEffects(itemPartial, processingDate))
				.reduce(0, Integer::sum);

		// (3) Apply effects of the activating itemPartial record to newly-owned entities.
		totalRows += activatingLifecycle.activateEffects(itemPartial, processingDate);

		LOG.debug("{}: {} total rows updated to activate", participationId, totalRows);

		activatingLifecycle.updateActivatedHistory(itemPartial, processingDate);

		return totalRows;
	}

	/**
	 * Call deactivate methods for each Participation type: call deactivate for this item;
	 * call deactivate effects for this item; and finally call activateEffects for all types.
	 */
	@Override
	public int deactivateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		ParticipationLifecycle deactivatingLifecycle = getLifecycle(itemPartial.getContentTypeId());

		LOG.debug("==== deactivating participation {} ====", participationId);

		int totalRows = participationCoreDao.setParticipationIsActive(participationId, false);

		// (1) Run type-specific queries for deactivating this Participation. Perform set up
		// for calling activateEffects() and deactivateEffects().
		totalRows += deactivatingLifecycle.deactivate(itemPartial, processingDate);

		int rowsAffected = participationCoreDao.updateLastOnSaleForDeactivatingProducts(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale base prices saved", participationId, rowsAffected);

		// (2) Remove effects of the deactivating record from the entities it owns.
		totalRows += deactivatingLifecycle.deactivateEffects(itemPartial, processingDate);

		// (3) Apply effects of any active Participations that are taking ownership of the entities
		// owned by the deactivating record.
		totalRows += lifecyclesByContentType.values().stream()
				.map(lifecycle -> lifecycle.activateEffects(itemPartial, processingDate))
				.reduce(0, Integer::sum);

		LOG.debug("{}: {} total rows updated to deactivate", participationId, totalRows);

		deactivatingLifecycle.updateDeactivatedHistory(itemPartial, processingDate);

		return totalRows;
	}

	/**
	 * Call the lifecycle unpublish method for the type of the given Participation to handle
	 * deleting effect-specific records it added when it published the Participation.
	 */
	@Override
	public int unpublishByType(ParticipationItemPartial itemPartial, Date processingDate) {
		LOG.debug("==== unpublishing participation {} ====", itemPartial.getParticipationId());
		int rowsAffected = getLifecycle(itemPartial.getContentTypeId()).unpublish(itemPartial, processingDate);
		LOG.debug("{}: {} total rows deleted to unpublish participation", itemPartial.getParticipationId(), rowsAffected);
		return rowsAffected;
	}

	@Override
	public Boolean getParticipationIsActive(Integer participationId) {
		return participationCoreDao.getParticipationIsActive(participationId);
	}


	@Override
	public ParticipationItemPartial getParticipationItemPartial(int participationId) {
		return participationCoreDao.getParticipationItemPartial(participationId);
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
	 * Get a lifecycle instance for the type of the given Participation. For compatibility
	 * with records with a null contentTypeId, value defaults to "participation@1". A validation
	 * exception is thrown if no content type with that id exists, or if no lifecycle bean
	 * for that content type was registered in lifecyclesByContentType.
	 */
	private ParticipationLifecycle getLifecycle(Integer contentTypeId) {
		if (contentTypeId != null) {
			ParticipationContentType contentType = ParticipationContentType.fromContentTypeId(contentTypeId);
			if (lifecyclesByContentType.containsKey(contentType)) {
				return lifecyclesByContentType.get(contentType);
			}
		}
		throw new ValidationException(ContentErrorMessage.INVALID_PARTICIPATION_CONTENT_TYPE.toString());
	}

}
