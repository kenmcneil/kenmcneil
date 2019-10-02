package com.ferguson.cs.product.test.attribute;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.product.dao.attribute.AttributeDataAccess;
import com.ferguson.cs.utilities.ArgumentAssert;

public class AttributeTestUtilities {

	final AttributeDataAccess  attributeDataAccess;

	public AttributeTestUtilities(AttributeDataAccess  attributeDataAccess) {
		this.attributeDataAccess = attributeDataAccess;
	}

	public UnitOfMeasure insertUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		ArgumentAssert.notNull(unitOfMeasure, "Unit of measure");
		return attributeDataAccess.saveUnitOfMeasure(unitOfMeasure);
	}
	public AttributeDefinition insertAttributeDefinition(AttributeDefinition definition) {
		ArgumentAssert.notNull(definition, "definition");
		return attributeDataAccess.saveAttributeDefinition(definition);
	}

}
