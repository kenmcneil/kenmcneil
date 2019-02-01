package com.ferguson.cs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This interface is used to force the ID to a String datatype and to insure the implementing classes have the standard auditing columns.
 *
 * @author tyler.vangorder
 */
 @Document
public interface PersistentDocument extends Serializable {

	/**
	 * The unique persistent ID.
	 *
	 * @param id
	 */
	void setId(String id);
	String getId();

	/**
	 * The timestamp indicating when this document was created.
	 *
	 * @param timestamp
	 */
	@CreatedDate
	@ReadOnlyProperty
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
	@ReadOnlyProperty
	void setLastModifiedTimestamp(LocalDateTime timestamp);

	/**
	 * The timestamp indicating when this document was last updated.
	 */
	LocalDateTime getLastModifiedTimestamp();

	/**
	 * Used to record a record version and prevent dirty writes.
	 *
	 * @param version
	 */
	@Version
	void setVersion(Long version);

	/**
	 * Used to record a record version and prevent dirty writes.
	 *
	 */
	Long getVersion();

}
