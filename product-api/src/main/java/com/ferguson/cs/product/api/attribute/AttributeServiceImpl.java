package com.ferguson.cs.product.api.attribute;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;
import com.ferguson.cs.utilities.ArgumentAssert;

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
	public Optional<UnitOfMeasure> getUnitOfMeasure(String uomCode) {
		ArgumentAssert.notNullOrEmpty(uomCode, "code");
		return unitOfMeasureRepository.findByCode(uomCode);
	}

	@Override
	public UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		ArgumentAssert.notNull(unitOfMeasure, "unit of measure");
		ArgumentAssert.notNullOrEmpty(unitOfMeasure.getCode(), "code");
		ArgumentAssert.notNullOrEmpty(unitOfMeasure.getName(), "name");

		return unitOfMeasureRepository.save(unitOfMeasure);
	}

	@Override
	public void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		ArgumentAssert.notNull(unitOfMeasure, "unit of measure");
		ArgumentAssert.notNullOrEmpty(unitOfMeasure.getId(), "ID");

		unitOfMeasureRepository.delete(unitOfMeasure);
	}

	@Override
	public Optional<AttributeDefinition> getAttributeDefinition(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		return attributeDefinitionRepository.findByCode(code);
	}

	@Override
	public AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition) {

		ArgumentAssert.notNull(attributeDefinition, "attribute definition");
		ArgumentAssert.notNullOrEmpty(attributeDefinition.getCode(), "code.");
		ArgumentAssert.notNullOrEmpty(attributeDefinition.getDescription(), "description.");
		ArgumentAssert.notNull(attributeDefinition.getDatatype(), "datatype.");
		if (attributeDefinition.getUnitOfMeasure() != null && StringUtils.hasText(attributeDefinition.getUnitOfMeasure().getId())) {
			if (!unitOfMeasureRepository.findById(attributeDefinition.getUnitOfMeasure().getId()).isPresent()) {
				throw new ResourceNotFoundException("The unit of measure associated with the attribute definition does not exist in the database.");
			}
		}
		return attributeDefinitionRepository.save(attributeDefinition);
	}

	@Override
	public void deleteAttributeDefinition(AttributeDefinition attributeDefinition) {
		ArgumentAssert.notNull(attributeDefinition, "attribute definition");
		ArgumentAssert.notNullOrEmpty(attributeDefinition.getId(), "ID");

		attributeDefinitionRepository.delete(attributeDefinition);
	}
}
