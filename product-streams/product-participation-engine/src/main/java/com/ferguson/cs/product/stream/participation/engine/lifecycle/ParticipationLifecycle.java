package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * Each type of Participation will implement this interface to handle its specific effects.
 * The ParticipationLifecycleService calls lifecycle methods for each Participation processed by the engine.
 */
public interface ParticipationLifecycle {
	/**
	 * Implementer must return the content type it handles.
	 */
	String getContentType();

	/**
	 * Publish the Participation record to SQL. Should return the sum of rows
	 * modified in all DB operations (for logging). The publish handler is fully responsible
	 * for inserting or updating all of its data that needs to be stored in SQL.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int publish(ParticipationItem item, Date processingDate);

	/**
	 * Activate the Participation's effects. Should return the sum of rows modified in
	 * all DB operations (for logging).
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int activate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Apply effects for any entities becoming owned, such as participationProducts. The implementing
	 * method must only apply effects for entities in Participations of its type; i.e. filter
	 * effect queries by contentTypeId.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int activateEffects(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Deactivate the Participation's effects. Should return the sum of rows modified
	 * in all DB operations (for logging).
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int deactivate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Deactivate effects on any records becoming owned by the activating or deactivating Participation.
	 * The implementing method must only apply effects for entities in Participations of its type; i.e.
	 * filter effect queries by contentTypeId.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Unpublish the Participation by removing its data from SQL. Should return the sum of
	 * rows modified in all DB operations (for logging).
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int unpublish(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Traverse the content map and return value of the last key in the path, or null.
	 */
	static <T> T getAtPath(ParticipationItem item, String[] path) {
		return item.getContent() == null ? null : getAtPath(item.getContent(), path);
	}

	/**
	 * Traverse the given map by the given path and return value of the last key in
	 * the path, or null.
	 */
	@SuppressWarnings("unchecked")
	static  <T> T getAtPath(Map<String, ?> content, String[] path) {
		Map<String, ?> currentObject = content;

		for (int i = 0; i < path.length; i++) {
			Object val = currentObject.get(path[i]);

			if (i == path.length - 1) {
				return (T) val;
			}

			if (!(val instanceof Map<?, ?>)) {
				return null;
			}

			currentObject = (Map<String, ?>) val;
		}

		return null;
	}
}
