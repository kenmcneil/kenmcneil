package com.ferguson.cs.productapi.attribute;

import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface AttributeDataAccess {

	UnitOfMeasure getUnitOfMeasure(String uomCode);

	UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure);

	void deleteUnitOfMeasure(String uomCode);

}
