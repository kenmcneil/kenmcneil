package com.ferguson.cs.productapi.attribute;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.attribute.AttributeDefinition;
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

	@Override
	public AttributeDefinition getAttributeDefinition(String code) {
		return attributeDataAccess.getAttributeDefinition(code);
	}

	@Override
	public AttributeDefinition saveAttributeDefinition(AttributeDefinition attributeDefinition) {
		return attributeDataAccess.saveAttributeDefinition(attributeDefinition);
	}

	@Override
	public void deleteAttributeDefinition(String code) {
		attributeDataAccess.deleteAttributeDefinition(code);
	}
}
