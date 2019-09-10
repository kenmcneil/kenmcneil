package com.ferguson.cs.product.api.attribute;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.product.dao.attribute.AttributeDataAccess;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class AttributeServiceImpl implements AttributeService {

	private final AttributeDataAccess attributeDataAccess;
	

	public AttributeServiceImpl(AttributeDataAccess attributeDataAccess) {

		this.attributeDataAccess = attributeDataAccess;
	}

	@Override
	public List<UnitOfMeasure> findUnitOfMeasureList(IdCodeCriteria criteria) {
		return attributeDataAccess.findUnitOfMeasureList(criteria);
	}

	@Override
	public Optional<UnitOfMeasure> getUnitOfMeasureByCode(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		List<UnitOfMeasure> results = attributeDataAccess.findUnitOfMeasureList(IdCodeCriteria.builder().code(code).build());
		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}		
	}

	@Override
	public Optional<UnitOfMeasure> getUnitOfMeasureById(Integer id) {
		ArgumentAssert.notNull(id, "id");
		List<UnitOfMeasure> results = attributeDataAccess.findUnitOfMeasureList(IdCodeCriteria.builder().id(id).build());
		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}		
	}
	
	
	@Override
	public UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		ArgumentAssert.notNull(unitOfMeasure, "unit of measure");
		ArgumentAssert.notNullOrEmpty(unitOfMeasure.getCode(), "code");
		ArgumentAssert.notNullOrEmpty(unitOfMeasure.getName(), "name");

		return attributeDataAccess.saveUnitOfMeasure(unitOfMeasure);
	}

	@Override
	public void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		ArgumentAssert.notNull(unitOfMeasure, "unitOfMeasure");
		ArgumentAssert.notNull(unitOfMeasure.getId(), "unitOfMeasure.id");

		attributeDataAccess.deleteUnitOfMeasure(unitOfMeasure);
	}

	@Override
	public List<AttributeDefinition> findAttributeDefinitionList(AttributeDefinitionCriteria criteria) {
		return attributeDataAccess.findAttributeDefinitionList(criteria);
	}
	
	@Override
	public Optional<AttributeDefinition> getAttributeDefinitionByCode(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		List<AttributeDefinition> results = attributeDataAccess.findAttributeDefinitionList(AttributeDefinitionCriteria.builder().attributeDefinitionCode(code).build());
		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}
		
	}

	@Override
	public Optional<AttributeDefinition> getAttributeDefinitionById(Integer id) {
		ArgumentAssert.notNull(id, "id");
		List<AttributeDefinition> results = attributeDataAccess.findAttributeDefinitionList(AttributeDefinitionCriteria.builder().attributeDefinitionId(id).build());
		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}
		
	}
	
	@Override
	public AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition) {

		ArgumentAssert.notNull(attributeDefinition, "attribute definition");
		ArgumentAssert.notNullOrEmpty(attributeDefinition.getCode(), "code.");
		ArgumentAssert.notNullOrEmpty(attributeDefinition.getDescription(), "description.");
		ArgumentAssert.notNull(attributeDefinition.getDatatype(), "datatype.");
		if (attributeDefinition.getUnitOfMeasure() != null && (attributeDefinition.getUnitOfMeasure().getId() != null)) {
			if (!getUnitOfMeasureById(attributeDefinition.getUnitOfMeasure().getId()).isPresent()) {
				throw new ResourceNotFoundException("The unit of measure associated with the attribute definition does not exist in the database.");
			}
		}
		return attributeDataAccess.saveAttributeDefinition(attributeDefinition);
	}

	@Override
	public void deleteAttributeDefinition(AttributeDefinition attributeDefinition) {
		ArgumentAssert.notNull(attributeDefinition, "attribute definition");
		ArgumentAssert.notNull(attributeDefinition.getId(), "ID");

		attributeDataAccess.deleteAttributeDefinition(attributeDefinition);
	}
}
