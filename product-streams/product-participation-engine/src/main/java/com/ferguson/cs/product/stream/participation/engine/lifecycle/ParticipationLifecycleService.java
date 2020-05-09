package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ContentErrorMessage;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ValidationException;

import lombok.RequiredArgsConstructor;

/**
 * Given a ParticipationItem, returns the correct lifecycle strategy bean based on the
 * content type, which is the schema name of the content type definition used to create
 * the record's content.
 */
@RequiredArgsConstructor
public class ParticipationLifecycleService {

	public static final String CONTENT_MAP_KEY_TYPE = "_type";

	private final ParticipationV1Lifecycle participationV1Lifecycle;

	/**
	 * Get the content._type return with the minor and patch version removed, in the form
	 * "content-type@majorVersion", e.g. "participation@1". Return null if not present.
	 */
	public String getContentTypeNameWithMajorVersion(ParticipationItem item) {
		if (item.getContent() != null && item.getContent().containsKey(CONTENT_MAP_KEY_TYPE)) {
			String fullType = (String) item.getContent().get(CONTENT_MAP_KEY_TYPE);
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
	public String getContentType(ParticipationItem item) {
		if (item.getContent() != null && item.getContent().containsKey(CONTENT_MAP_KEY_TYPE)) {
			return (String) item.getContent().get(CONTENT_MAP_KEY_TYPE);
		}
		return null;
	}

	/**
	 * Get a lifecycle instance for the type of the given Participation.
	 */
	public ParticipationLifecycleBase getLifecycleFor(String contentType) {
		if (contentType == null) {
			// All records that existed prior to adding the content type are of type participation@1.
			return participationV1Lifecycle;
		}

		if (contentType.startsWith(ParticipationV1Lifecycle.NAME_WITH_MAJOR_VERSION + ".")) {
			return participationV1Lifecycle;
		}

		throw new ValidationException(ContentErrorMessage.INVALID_PARTICIPATION_CONTENT_TYPE.toString());
	}

	/**
	 * Call activate methods for each Participation type. Each lifecycle class should handle activating products
	 * and other changes needed for rows in the owner-changes table where newParticipationId is not null.
	 * Each lifecycle class must only process products in Participations of its type; i.e filter by contentTypeId.
	 * Expects that the product owner-changes table is initialized.
	 */
	public int activateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		// Run all type-specific activation queries.
		int affectedRows = participationV1Lifecycle.activate(itemPartial, processingDate);
		// affectedRows += itemizedDiscountsV1Lifecycle.activate(itemPartial, processingDate);
		// ...
		return affectedRows;
	}

	/**
	 * Call deactivate methods for each Participation type. Each lifecycle class should handle deactivating products
	 * and other changes needed for rows in the owner-changes table where oldParticipationId is not null.
	 * Each lifecycle class must only process products in Participations of its type; i.e filter by contentTypeId.
	 * Expects that the product owner-changes table is initialized.
	 */
	public int deactivateByType(ParticipationItemPartial itemPartial, Date processingDate) {
		// Run all type-specific activation queries.
		int affectedRows = participationV1Lifecycle.deactivate(itemPartial, processingDate);
		// affectedRows += itemizedDiscountsV1Lifecycle.deactivate(itemPartial, processingDate);
		// ...
		return affectedRows;
	}

	/**
	 * Call unpublish methods for each Participation type. Each lifecycle class should handle deleting records
	 * that it added when published.
	 * and other changes needed for rows in the owner-changes table where oldParticipationId is not null.
	 * Each lifecycle class must only process products in Participations of its type; i.e filter by contentTypeId.
	 * Expects that the product owner-changes table is initialized.
	 */
	public int unpublishByType(ParticipationItemPartial itemPartial, Date processingDate) {
		// Run all type-specific activation queries.
		int affectedRows = participationV1Lifecycle.deactivate(itemPartial, processingDate);
		// affectedRows += itemizedDiscountsV1Lifecycle.deactivate(itemPartial, processingDate);
		// ...
		return affectedRows;
	}
}
