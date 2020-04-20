package com.ferguson.cs.product.stream.participation.engine.model;

/**
 * For tracking engine state. Only has meaning in a record when the record status is PUBLISHED.
 */
public enum ParticipationItemUpdateStatus {
	NEEDS_UPDATE,
	NEEDS_CLEANUP,
	NEEDS_UNPUBLISH;
}
