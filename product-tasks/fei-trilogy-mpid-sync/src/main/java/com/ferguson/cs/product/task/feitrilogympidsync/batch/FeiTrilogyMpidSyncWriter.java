package com.ferguson.cs.product.task.feitrilogympidsync.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.product.task.feitrilogympidsync.data.FeiTrilogyMpidSyncService;
import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;

public class FeiTrilogyMpidSyncWriter implements ItemWriter<FeiTrilogyMpidSync> {

	private final FeiTrilogyMpidSyncService feiTrilogyMpidSyncService;

	public FeiTrilogyMpidSyncWriter(
			FeiTrilogyMpidSyncService feiTrilogyMpidSyncService) {
		this.feiTrilogyMpidSyncService = feiTrilogyMpidSyncService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiTrilogyMpidSync> items) throws Exception {

		for (FeiTrilogyMpidSync item : (List<FeiTrilogyMpidSync>) items) {
			item.setInTrilogy(Boolean.TRUE);
			feiTrilogyMpidSyncService.updateFeiMpidTrilogyFlag(item);
		}
	}
}
