package com.ferguson.cs.product.stream.participation.engine.construct;

import com.ferguson.cs.product.stream.participation.engine.model.PagedSearchResults;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;

public interface ParticipationItemRepositoryCustom {
	void updateParticipationItemStatusFields(ParticipationItem updateItem) throws Exception;

	/**
	 * Query the participation item collection
	 * @param criteria {@link ParticipationItemSearchCriteria} object of the search criteria
	 * @return A list of objects of type {@link ParticipationItem}
	 */
	PagedSearchResults<ParticipationItem> findMatchingParticipationItems(ParticipationItemSearchCriteria criteria);
}
