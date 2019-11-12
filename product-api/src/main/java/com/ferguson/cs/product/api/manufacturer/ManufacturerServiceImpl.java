package com.ferguson.cs.product.api.manufacturer;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.model.manufacturer.ManufacturerCriteria;
import com.ferguson.cs.product.dao.manufacturer.ManufacturerDataAccess;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

	private final ManufacturerDataAccess manufacturerDataAccess;

	public ManufacturerServiceImpl(ManufacturerDataAccess manufacturerDataAccess) {
		this.manufacturerDataAccess = manufacturerDataAccess;
	}

	@Override
	public Optional<Manufacturer> getManufacturerById(Integer id) {
		ArgumentAssert.notNull(id, "id");

		List<Manufacturer> results = manufacturerDataAccess.findManufacturers(ManufacturerCriteria.builder()
			.manufacturerId(id)
			.build());

		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}
	}

	@Override
	public List<Manufacturer> findManufacturers(ManufacturerCriteria criteria) {
		ArgumentAssert.notNull(criteria, "criteria");
		return manufacturerDataAccess.findManufacturers(criteria);
	}

	@Override
	public Manufacturer saveManufacturer(Manufacturer manufacturer) {
		ArgumentAssert.notNull(manufacturer, "manufacturer");
		ArgumentAssert.notNullOrEmpty(manufacturer.getName(), "manufacturer.name");
		ArgumentAssert.notNull(manufacturer.getDescription(),  "manufacturer.description");

		return manufacturerDataAccess.saveManufacturer(manufacturer);
	}

	@Override
	public void deleteManufacturer(final Manufacturer manufacturer) {
		ArgumentAssert.notNull(manufacturer, "manufacturer");
		ArgumentAssert.notNull(manufacturer.getId(), "manufacturer.id");
		ArgumentAssert.notNull(manufacturer.getVersion(), "manufacturer.version");
		manufacturerDataAccess.deleteManufacturer(manufacturer);
	}
}
