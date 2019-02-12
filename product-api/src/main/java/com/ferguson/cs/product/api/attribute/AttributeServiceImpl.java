package com.ferguson.cs.product.api.attribute;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;

@Service
public class AttributeServiceImpl implements AttributeService {

	private final UnitOfMeasureRepository unitOfMeasureRepository;
	private final AttributeDefinitionRepository attributeDefinitionRepository;

	public AttributeServiceImpl(
			UnitOfMeasureRepository unitOfMeasureRepository,
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

	@Override
	public AttributeDefinition getAttributeDefinition(String code) {
		Assert.hasText(code, "The code for the attribute definition is required to retrieve the record.");
		AttributeDefinition attributeDefinition = attributeDefinitionRepository.findByCode(code);
		if (attributeDefinition == null) {
			throw new ResourceNotFoundException(String.format("The attribute definition code [%s] does not exist in the database.", code));
		}
		return attributeDefinition;
	}

	@Override
	public AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition) {
		Assert.notNull(attributeDefinition, "The attribute definition must be provided to update the record.");
		Assert.hasText(attributeDefinition.getCode(), "The attribute definition must have a code.");
		Assert.hasText(attributeDefinition.getDescription(), "The attribute definition must have a description.");
		Assert.notNull(attributeDefinition.getDatatype(), "The attribute definition must have a datatype.");
		if (attributeDefinition.getUnitOfMeasure() != null && StringUtils.hasText(attributeDefinition.getUnitOfMeasure().getId())) {
			if (!unitOfMeasureRepository.findById(attributeDefinition.getUnitOfMeasure().getId()).isPresent()) {
				throw new ResourceNotFoundException("The unit of measure associated with the attribute definition does not exist in the database.");
			}
		}
		return attributeDefinitionRepository.save(attributeDefinition);
	}

	@Override
	public void deleteAttributeDefinition(String code) {
		AttributeDefinition attributeDefinition = getAttributeDefinition(code);
		attributeDefinitionRepository.delete(attributeDefinition);
	}
}
