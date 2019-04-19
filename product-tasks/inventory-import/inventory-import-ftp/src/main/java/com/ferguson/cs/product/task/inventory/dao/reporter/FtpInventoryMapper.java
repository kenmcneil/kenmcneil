package com.ferguson.cs.product.task.inventory.dao.reporter;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

@Mapper
public interface FtpInventoryMapper {
	List<VendorFtpData> getVendorFtpData();
}
