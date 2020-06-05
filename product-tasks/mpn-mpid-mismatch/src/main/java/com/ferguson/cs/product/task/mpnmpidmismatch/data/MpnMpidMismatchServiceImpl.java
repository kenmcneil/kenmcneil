package com.ferguson.cs.product.task.mpnmpidmismatch.data;

import org.springframework.stereotype.Service;

@Service("mpnMpidMismatchService")
public class MpnMpidMismatchServiceImpl implements MpnMpidMismatchService {

	private final MpnMpidMismatchDao mpnMpidMismatchDao;

	public MpnMpidMismatchServiceImpl(MpnMpidMismatchDao mpnMpidMismatchDao) {
		this.mpnMpidMismatchDao = mpnMpidMismatchDao;
	}

	@Override
	public Integer insertMissingFeiMpidRecords() {
		return mpnMpidMismatchDao.insertMissingFeiMpidRecords();
	}

}
