package com.ferguson.cs.product.dao.manufacturer;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.data.AbstractDataAccess;
import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.model.manufacturer.ManufacturerCriteria;

@Repository
public class ManufacturerDataAccessImpl extends AbstractDataAccess  implements ManufacturerDataAccess {

	private ManufacturerMapper manufacturerMapper;


	public ManufacturerDataAccessImpl(ManufacturerMapper manufacturerMapper) {
		super();
		this.manufacturerMapper = manufacturerMapper;
	}

	@Override
	public List<Manufacturer> findManufacturers(ManufacturerCriteria criteria) {
		return manufacturerMapper.findManufacturers(criteria);
	}

	@Override
	@Transactional
	public Manufacturer saveManufacturer(Manufacturer manufacturer) {
		return saveEntity(manufacturer, manufacturerMapper::insertManufacturer, manufacturerMapper::updateManufacturer);
	}

	@Override
	public void deleteManufacturer(Manufacturer manufacturer) {
		deleteEntity(manufacturer, manufacturerMapper::deleteManufacturer);
	}

}
