package com.ferguson.cs.product.task.feitrilogympidsync.data;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

@Service("feiTrilogyMpidSyncService")
public class FeiTrilogyMpidSyncServiceImpl implements FeiTrilogyMpidSyncService {

	private final FeiTrilogyMpidSyncDao feiTrilogyMpidSyncDao;

	public FeiTrilogyMpidSyncServiceImpl(FeiTrilogyMpidSyncDao feiTrilogyMpidSyncDao) {
		this.feiTrilogyMpidSyncDao = feiTrilogyMpidSyncDao;
	}

	@Override
	public void updateFeiMpidTrilogyFlag(FeiTrilogyMpidSync feiTrilogyMpidSync) {
		feiTrilogyMpidSyncDao.updateFeiMpidTrilogyFlag(feiTrilogyMpidSync);
	}
}
