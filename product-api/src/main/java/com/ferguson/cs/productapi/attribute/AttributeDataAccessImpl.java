package com.ferguson.cs.productapi.attribute;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;

@Repository
public class AttributeDataAccessImpl implements AttributeDataAccess {

	private final UnitOfMeasureRepository unitOfMeasureRepository;
	private final AttributeDefinitionRepository attributeDefinitionRepository;

	public AttributeDataAccessImpl(UnitOfMeasureRepository unitOfMeasureRepository,
			AttributeDefinitionRepository attributeDefinitionRepository) {
		this.unitOfMeasureRepository = unitOfMeasureRepository;
		this.attributeDefinitionRepository = attributeDefinitionRepository;
	}

	@Override
	public UnitOfMeasure getUnitOfMeasure(String uomCode) {
		Assert.hasText(uomCode, "The code for the unit of measure is required to retrieve the record.");
		UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findByCode(uomCode);
		if (unitOfMeasure == null) {
			throw new ResourceNotFoundException(String.format("The unit of measure code [%s] does not exist in the database.", uomCode));
		}
		return unitOfMeasure;
	}

	@Override
	public UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		Assert.notNull(unitOfMeasure, "The unit of measure must be provided to update the record.");
		Assert.hasText(unitOfMeasure.getCode(), "The unit of measure must have a code.");
		Assert.hasText(unitOfMeasure.getName(), "The unit of measure must have a name..");
		return unitOfMeasureRepository.save(unitOfMeasure);
	}

	@Override
	public void deleteUnitOfMeasure(String uomCode) {
		UnitOfMeasure unitOfMeasure = getUnitOfMeasure(uomCode);
		unitOfMeasureRepository.delete(unitOfMeasure);
	}

}
