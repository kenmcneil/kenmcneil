package com.ferguson.cs.product.task.feitrilogympidsync.data;


import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

public interface FeiTrilogyMpidSyncDao {

	/**
	 * Update the feiMpid inTrilogy flag for the given input record
	 */
	void updateFeiMpidTrilogyFlag(FeiTrilogyMpidSync feiTrilogyMpidSync);

}
