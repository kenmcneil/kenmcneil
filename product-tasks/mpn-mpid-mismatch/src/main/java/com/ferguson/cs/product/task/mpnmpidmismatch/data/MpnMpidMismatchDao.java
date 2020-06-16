package com.ferguson.cs.product.task.mpnmpidmismatch.data;

public interface MpnMpidMismatchDao {

	/**
	 * This method will populate all the missing feiMPID records where there is a
	 * vendor_mapping mpn but no corresponding mpid
	 */
	Integer insertMissingFeiMpidRecords();
}
