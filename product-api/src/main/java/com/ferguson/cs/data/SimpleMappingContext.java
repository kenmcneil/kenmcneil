package com.ferguson.cs.data;

import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

public class SimpleMappingContext extends AbstractMappingContext< SimplePersistentEntity<?>, SimplePersistentProperty> {

	
	public SimpleMappingContext() {
		setSimpleTypeHolder(SimpleTypeHolder.DEFAULT);
	}

	@Override
	protected SimplePersistentProperty createPersistentProperty(Property property, SimplePersistentEntity<?> owner,
			SimpleTypeHolder simpleTypeHolder) {
		return new SimplePersistentPropertyImpl(property, owner, simpleTypeHolder);
	}

	@Override
	protected <T> SimplePersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
		return new SimplePersistentEntityImpl<>(typeInformation);
	}

}
