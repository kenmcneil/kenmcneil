package com.ferguson.cs.product.task.mpnmpidmismatch.data;

import org.springframework.stereotype.Repository;

@Repository
public class MpnMpidMismatchDaoImpl implements MpnMpidMismatchDao {

	private MpnMpidMismatchMapper mpnMpidMismatchMapper;

	public MpnMpidMismatchDaoImpl(MpnMpidMismatchMapper mpnMpidMismatchMapper) {
		this.mpnMpidMismatchMapper = mpnMpidMismatchMapper;
	}

	@Override
	public Integer insertMissingFeiMpidRecords() {
		return mpnMpidMismatchMapper.insertMissingFeiMpidRecords();
	}

}
