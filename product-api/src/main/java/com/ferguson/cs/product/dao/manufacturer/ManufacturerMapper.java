package com.ferguson.cs.product.dao.manufacturer;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.model.manufacturer.ManufacturerCriteria;

@Mapper
public interface ManufacturerMapper {

	List<Manufacturer> findManufacturers(ManufacturerCriteria criteria);
	int insertManufacturer(Manufacturer manufacturer);
	int updateManufacturer(Manufacturer manufacturer);
	int deleteManufacturer(Manufacturer manufacturer);
}
