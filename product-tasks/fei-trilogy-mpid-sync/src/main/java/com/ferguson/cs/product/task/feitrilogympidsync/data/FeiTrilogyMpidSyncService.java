package com.ferguson.cs.product.task.feitrilogympidsync.data;

import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

public interface FeiTrilogyMpidSyncService {

	/**
	 * Update the feiMpid inTrilogy column for the given input record
	 */
	void updateFeiMpidTrilogyFlag(FeiTrilogyMpidSync feiTrilogyMpidSync);
}
