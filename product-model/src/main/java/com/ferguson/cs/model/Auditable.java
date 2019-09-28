package com.ferguson.cs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * This interface is used standard the properties used for auditing information : create and update time stamps.
 *
 * @author tyler.vangorder
 */
public interface Auditable extends Serializable {

	/**
	 * The timestamp indicating when this document was created.
	 *
	 * @param timestamp
	 */
	@CreatedDate
	void setCreatedTimestamp(LocalDateTime timestamp);

	/**
	 * The timestamp indicating when this document was created.
	 */
	LocalDateTime getCreatedTimestamp();

	/**
	 * The timestamp indicating when this document was last updated.
	 *
	 * @param timestamp
	 */
	@LastModifiedDate
	void setLastModifiedTimestamp(LocalDateTime timestamp);

	/**
	 * The timestamp indicating when this document was last updated.
	 */
	LocalDateTime getLastModifiedTimestamp();

}
