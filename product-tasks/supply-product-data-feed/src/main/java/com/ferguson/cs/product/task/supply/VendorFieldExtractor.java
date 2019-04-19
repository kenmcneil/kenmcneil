package com.ferguson.cs.product.task.supply;

import org.springframework.batch.item.file.transform.FieldExtractor;
import com.ferguson.cs.product.task.supply.model.Vendor;

public class VendorFieldExtractor implements FieldExtractor<Vendor> {
	@Override
	public Object[] extract(Vendor vendor) {
		Object[] fields = new Object[11];
		fields[0] = vendor.getId();
		fields[1] = vendor.getVendorName();
		fields[2] = vendor.getVendorId();
		fields[3] = vendor.getVendorAddress();
		fields[4] = vendor.getVendorCity();
		fields[5] = vendor.getVendorState();
		fields[6] = vendor.getVendorZipCode();
		fields[7] = vendor.getContactPhone();
		fields[8] = vendor.getContactFax();
		fields[9] = vendor.getLastUpdated();
		/*'active' csv column. Database column is 'inactive' and nullable because some people just want to watch
		 the world burn. We assume here that null means vendor is active, because that's probably the only
		 sane thing to do
		 */
		fields[10] = !Boolean.TRUE.equals(vendor.getInactive());

		return fields;
	}
}
