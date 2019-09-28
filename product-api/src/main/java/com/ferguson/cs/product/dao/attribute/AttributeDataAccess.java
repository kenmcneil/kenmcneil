package com.ferguson.cs.product.dao.attribute;

import java.util.List;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface AttributeDataAccess {

	List<UnitOfMeasure> findUnitOfMeasureList(IdCodeCriteria build);
	UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure);
	void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure);

	List<AttributeDefinition> findAttributeDefinitionList(AttributeDefinitionCriteria build);
	AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition);
	void deleteAttributeDefinition (AttributeDefinition  attributeDefinition);	
}
