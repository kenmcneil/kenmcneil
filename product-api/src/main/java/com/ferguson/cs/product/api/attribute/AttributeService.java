package com.ferguson.cs.product.api.attribute;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface AttributeService {

	List<UnitOfMeasure> findUnitOfMeasureList(IdCodeCriteria criteria);
	Optional<UnitOfMeasure> getUnitOfMeasureByCode(String code);
	Optional<UnitOfMeasure> getUnitOfMeasureById(Integer id);
	UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure);
	void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure);

	List<AttributeDefinition> findAttributeDefinitionList(AttributeDefinitionCriteria criteria);
	Optional<AttributeDefinition> getAttributeDefinitionByCode(String code);
	Optional<AttributeDefinition> getAttributeDefinitionById(Integer id);
	AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition);
	void deleteAttributeDefinition(AttributeDefinition attributeDefinition);

}
