package com.ferguson.cs.product.dao.attribute;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.data.AbstractDataAccess;
import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinitionValue;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.utilities.ArgumentAssert;

@Repository
public class AttributeDataAccessImpl extends AbstractDataAccess implements AttributeDataAccess {

	private final AttributeDefinitionMapper attributeDefinitionMapper;

	public AttributeDataAccessImpl(AttributeDefinitionMapper attributeDefinitionMapper) {
		this.attributeDefinitionMapper = attributeDefinitionMapper;
	}

	@Override
	public List<UnitOfMeasure> findUnitOfMeasureList(IdCodeCriteria criteria) {
		return attributeDefinitionMapper.findUnitOfMeasureList(criteria);
	}

	@Transactional
	@Override
	public UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		return saveEntity(unitOfMeasure, attributeDefinitionMapper::insertUnitOfMeasure, attributeDefinitionMapper::updateUnitOfMeasure);
	}

	@Transactional
	@Override
	public void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		deleteEntity(unitOfMeasure, attributeDefinitionMapper::deleteUnitOfMeasure);
	}

	@Override
	public List<AttributeDefinition> findAttributeDefinitionList(AttributeDefinitionCriteria criteria) {
		return attributeDefinitionMapper.findAttributeDefinitionList(criteria);
	}

	@Transactional
	@Override
	public AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition) {


		AttributeDefinition saved = saveEntity(attributeDefinition, attributeDefinitionMapper::insertAttributeDefinition, attributeDefinitionMapper::updateAttributeDefinition);

		//Save the enumerated values.
		for (AttributeDefinitionValue value : saved.getEnumeratedValues()) {
			if (isNew(value)) {
				attributeDefinitionMapper.insertAttributeDefinitionValue(value, saved.getId());
			} else {
				attributeDefinitionMapper.updateAttributeDefinitionValue(value, saved.getId());
			}
		}
		//Delete any values that are not in the current, in-memory list.
		attributeDefinitionMapper.deleteAttributeDefinitionValues(saved.getId(), saved.getEnumeratedValues());
		return saved;
	}

	@Transactional
	@Override
	public void deleteAttributeDefinition(AttributeDefinition attributeDefinition) {
		ArgumentAssert.notNull(attributeDefinition, "attributeDefinition");
		ArgumentAssert.notNull(attributeDefinition.getId(), "attributeDefinition.id");

		//delete all values first.
		attributeDefinitionMapper.deleteAttributeDefinitionValues(attributeDefinition.getId(), null);
		deleteEntity(attributeDefinition, attributeDefinitionMapper::deleteAttributeDefinition);
	}
}
