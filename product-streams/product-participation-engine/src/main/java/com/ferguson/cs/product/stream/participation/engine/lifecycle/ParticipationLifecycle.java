package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * Each type of Participation will implement this interface to handle its specific effects.
 * The ParticipationLifecycleService calls lifecycle methods for each Participation
 * processed by the engine.
 *
 * Each type of Participation is responsible for keeping track of entities owned by
 * Participation records of its type. A Participation type must apply and remove its
 * effects as ownership of its entities change due to activation or deactivation events.
 * Some Participation types may work together and allow overriding and fallback of
 * entities, such as calculated discounts and itemized discounts. Some types may be
 * completely independent from all other types.
 * The Participation's contentTypeId is used in lifecycle events to call its
 * lifecycle event handlers.
 *
 * Each handler should return the sum of rows modified in all DB operations for
 * logging/debugging.
 */
public interface ParticipationLifecycle {
	/**
	 * Implementer must return the content type it handles.
	 */
	ParticipationContentType getContentType();

	/**
	 * Publish the Participation record to SQL. The publish handler is fully responsible
	 * for inserting or updating all of its data that needs to be stored in SQL.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int publish(ParticipationItem item, Date processingDate);

	/**
	 * Activate the Participation and set up for applying effects in activateEffects
	 * handler.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int activate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Apply effects for any entities becoming owned, such as participationProducts.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int activateEffects(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Deactivate the Participation, and prepare to deactivate its effects on owned
	 * entities.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int deactivate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Remove effects on entities that are losing ownership either from deactivation
	 * or from an overriding higher priority Participation.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Unpublish the Participation by removing its data from SQL.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	int unpublish(ParticipationItemPartial itemPartial, Date processingDate);


	@Transactional(propagation = Propagation.MANDATORY)
	int publishToHistory(ParticipationItem item, Date processingDate);

	@Transactional(propagation = Propagation.MANDATORY)
	int updateActivatedHistory(ParticipationItemPartial itemPartial, Date processingDate);

	@Transactional(propagation = Propagation.MANDATORY)
	int updateDeactivatedHistory(ParticipationItemPartial itemPartial, Date processingDate);

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
