package com.ferguson.cs.product.task.feitrilogympidsync.data;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

@Repository
public class FeiTrilogyMpidSyncDaoImpl implements  FeiTrilogyMpidSyncDao {

	private FeiTrilogyMpidSyncMapper feiTrilogyMpidSyncMapper;

	public FeiTrilogyMpidSyncDaoImpl(FeiTrilogyMpidSyncMapper feiTrilogyMpidSyncMapper) {
		this.feiTrilogyMpidSyncMapper = feiTrilogyMpidSyncMapper;
	}

	@Override
	public void updateFeiMpidTrilogyFlag(FeiTrilogyMpidSync feiTrilogyMpidSync) {
		feiTrilogyMpidSyncMapper.updateFeiMpidTrilogyFlag(feiTrilogyMpidSync);
	}
}
