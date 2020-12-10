package com.ferguson.cs.product.task.feitrilogympidsync.data;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

@Mapper
public interface FeiTrilogyMpidSyncMapper {

	/**
	 * Update the feiMpid inTrilogy flag to 1 for the given MPID
	 */
	void updateFeiMpidTrilogyFlag(FeiTrilogyMpidSync feiTrilogyMpidSync);

	/**
	 * Return all mmc.product.feiMPID records with a matching departments.feiintegration.trilogyMPID mpid
	 * where feiMPID.inTrilogy = 0
	 *
	 * @return List<FeiTrilogyMpidSync>
	 */
	List<FeiTrilogyMpidSync> getTrilogyMpidsToUpdate();
}
