package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Mapper
public interface ParticipationCoreMapper {

	/**
	 * Determine if a participation is currently active.
	 * @param participationId The id of a participation.
	 * @return Returns true if a participation is currently active, else false.
	 */
	Boolean getParticipationIsActive(int participationId);

	/**
	 * Mark a participation as active or inactive.
	 * @param participationId The id of a participation.
	 * @param isActive Use true to indicate active, false to indicate inactive.
	 * @return The number of records modified.
	 */
	int setParticipationIsActive(int participationId, Boolean isActive);

	/**
	 * Create the participationOwnerChange temp table and fill it with the ownership
	 * changes caused by deactivating the specified participation.
	 * @param participationId The id of the deactivating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(int participationId);

	/**
	 * Create the participationOwnerChange temp table and fill it with the ownership
	 * changes caused by activating the specified participation.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(int participationId);

	/**
	 * Set ownership for products in P, only considering active participations.
	 * If P is active then it is considered for ownership otherwise ownership will go to
	 * other (if any) participations.
	 * @return The number of records modified.
	 */
	int addProductOwnershipForNewOwners();

	/**
	 * Set ownership of each uniqueId in the participation to other participations (if any).
	 * Does not change the isOwner value for the participation, because this is needed in
	 * subsequent queries to have the list of uniqueIds that are being updated.
	 * @return The number of records modified.
	 */
	int removeProductOwnershipForOldOwners();

	/**
	 * Set the saleId of each product becoming owned to the saleId of the participation taking
	 * ownership -- where participationOwnerChange.newParticipationId is not null.
	 * Set product sale ids to zero where there is no new owner (newParticipationId is null).
	 * @return The number of records modified.
	 */
	int activateAndDeactivateProductSaleIds();

	/**
	 * Update product.modified to trigger product cache update.
	 *
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int updateProductModifiedDates(Date processingDate, int userId);

	/**
	 * Remove all participationProduct records for the given
	 * participation id.
	 *
	 * @param participationId The id of the participation from which to delete products.
	 * @return The number of records modified.
	 */
	int deleteParticipationProducts(int participationId);

	/**
	 * Delete the participationItemPartial record for given participation id.
	 *
	 * @param participationId The participationId of the participationItemPartial record to delete.
	 * @return The number of records modified.
	 */
	int deleteParticipationItemPartial(int participationId);

	/**
	 * Get next participation that is pending activation at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 * Null minParticipationId value is allowed, and indicates no restriction.
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId);

	/**
	 * Get next participation that is expired at the given date.
	 * Returns Participation records that are expired whether active or not.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 * Null minParticipationId value is allowed, and indicates no restriction.
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId);

	int upsertParticipationItemPartial(ParticipationItemPartial itemPartial);

	/**
	 * Insert products for a Participation for the list of uniqueIds given in CSV format.
	 */
	int insertParticipationProducts(int participationId, String csvUniqueIds);

	@Select("SELECT participationId, saleId, startDate, endDate, lastModifiedUserId, isActive, contentTypeId" +
			" FROM mmc.product.participationItemPartial" +
			" WHERE participationId = #{participationId}")
	ParticipationItemPartial getParticipationItemPartial(int participationId);
}
