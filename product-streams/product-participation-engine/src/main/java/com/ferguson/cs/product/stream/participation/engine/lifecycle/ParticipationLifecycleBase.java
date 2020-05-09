package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Component
public abstract class ParticipationLifecycleBase {

	/**
	 * Publish the Participation record to SQL. Should return the sum of rows
	 * modified in all DB operations (for logging).
	 */
	public abstract int publish(ParticipationItem item, Date processingDate);

	/**
	 * Activate the Participation's effects. Should return the sum of rows modified in
	 * all DB operations (for logging).
	 */
	public abstract int activate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Deactivate the Participation's effects. Should return the sum of rows modified
	 * in all DB operations (for logging).
	 */
	public abstract int deactivate(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Unpublish the Participation by removing its data from SQL. Should return the sum of
	 * rows modified in all DB operations (for logging).
	 */
	public abstract int unpublish(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Traverse the content map and return value of the last key in the path, or null.
	 */
	protected <T> T getAtPath(ParticipationItem item, String[] path) {
		return item.getContent() == null ? null : getAtPath(item.getContent(), path);
	}

	/**
	 * Traverse the given map by the given path and return value of the last key in
	 * the path, or null.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getAtPath(Map<String, ?> content, String[] path) {
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
