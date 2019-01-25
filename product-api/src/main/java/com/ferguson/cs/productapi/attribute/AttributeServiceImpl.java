package com.ferguson.cs.productapi.attribute;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.attribute.UnitOfMeasure;

@Service
public class AttributeServiceImpl implements AttributeService {

	private final AttributeDataAccess attributeDataAccess;

	public AttributeServiceImpl(AttributeDataAccess attributeDataAccess) {
		this.attributeDataAccess = attributeDataAccess;
	}

	@Override
	public UnitOfMeasure getUnitOfMeasure(String uomCode) {
		return attributeDataAccess.getUnitOfMeasure(uomCode);
	}

	@Override
	public UnitOfMeasure saveUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		return attributeDataAccess.saveUnitOfMeasure(unitOfMeasure);
	}

	@Override
	public void deleteUnitOfMeasure(String uomCode) {
		attributeDataAccess.deleteUnitOfMeasure(uomCode);
	}
}
