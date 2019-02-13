package com.ferguson.cs.product.api.attribute;

import java.util.Optional;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface AttributeService {

	Optional<UnitOfMeasure> getUnitOfMeasure(String uomCode);
	UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure);
	void deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure);

	Optional<AttributeDefinition> getAttributeDefinition(String code);
	AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition);
	void deleteAttributeDefinition(AttributeDefinition attributeDefinition);

}
