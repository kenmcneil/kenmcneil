package com.ferguson.cs.product.task.mpnmpidmismatch.data;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.mpnmpidmismatch.model.MpnMpidProductItem;

@Mapper
public interface MpnMpidMismatchMapper {

	/*
	 * Get a list of all the records that don't have a corresponding feiMPID record
	 */
	List<MpnMpidProductItem> getMpnMpidMissingItems();

	/*
	 * Get a list of all the records that have a mismatch between vendor_mapping.mpn
	 * and feiMPID.mpid
	 */
	List<MpnMpidProductItem> getMpnMpidMismatchItems();

	/**
	 * This method will populate all the missing feiMPID records where there is a
	 * vendor_mapping mpn but no corresponding mpid
	 */
	Integer insertMissingFeiMpidRecords();
}
