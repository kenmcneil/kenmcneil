package com.ferguson.cs.product.dao.reporter;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.model.VendorFtpData;

@Mapper
public interface FtpInventoryMapper {
	List<VendorFtpData> getVendorFtpData();
}
