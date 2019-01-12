package com.ferguson.cs.product.dao.reporter;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.model.VendorFtpData;

@Repository
public class FtpInventoryDaoImpl implements FtpInventoryDao {

	private FtpInventoryMapper ftpInventoryMapper;

	@Autowired
	public void setFtpInventoryMapper(FtpInventoryMapper ftpInventoryMapper) {
		this.ftpInventoryMapper = ftpInventoryMapper;
	}

	@Override
	public List<VendorFtpData> getVendorFtpData() {
		return ftpInventoryMapper.getVendorFtpData().stream().map(this::sanitizeVendorFtpData).collect(Collectors.toList());
	}

	private VendorFtpData sanitizeVendorFtpData(VendorFtpData original) {
		VendorFtpData sanitized = new VendorFtpData(original);

		if (sanitized.getFtpPort() == null) {
			sanitized.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
		}

		if(sanitized.getFtpPassword() == null) {
			sanitized.setFtpPassword("");
		}

		if(StringUtils.isBlank(sanitized.getFtpPath())) {
			sanitized.setFtpPath("/");
		}

		if(!sanitized.getFtpPath().endsWith("/") && !sanitized.getFtpPath().endsWith("\\") ) {
			sanitized.setFtpPath(sanitized.getFtpPath() + "/");
		}
		return sanitized;
	}
}
