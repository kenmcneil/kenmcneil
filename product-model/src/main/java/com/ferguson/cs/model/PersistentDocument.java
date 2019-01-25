package com.ferguson.cs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * This interface is used to force the ID to a String datatype and to insure the implementing classes have the standard auditing columns.
 *
 * @author tyler.vangorder
 */
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
