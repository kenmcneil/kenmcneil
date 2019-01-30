package com.ferguson.cs.productapi.attribute;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface AttributeDataAccess {

	UnitOfMeasure getUnitOfMeasure(String uomCode);
	UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure);
	void deleteUnitOfMeasure(String uomCode);

	AttributeDefinition getAttributeDefinition(String code);
	AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition);
	void deleteAttributeDefinition(String code);

}
