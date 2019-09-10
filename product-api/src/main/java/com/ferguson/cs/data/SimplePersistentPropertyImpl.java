package com.ferguson.cs.data;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;

public class SimplePersistentPropertyImpl extends AnnotationBasedPersistentProperty<SimplePersistentProperty> implements SimplePersistentProperty {

	public SimplePersistentPropertyImpl(Property property, PersistentEntity<?, SimplePersistentProperty> owner,
			SimpleTypeHolder simpleTypeHolder) {
		super(property, owner, simpleTypeHolder);
	}

	@Override
	protected Association<SimplePersistentProperty> createAssociation() {
		throw new UnsupportedOperationException();		
	}
}
