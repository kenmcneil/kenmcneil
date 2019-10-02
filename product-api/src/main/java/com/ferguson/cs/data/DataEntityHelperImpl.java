package com.ferguson.cs.data;

import com.ferguson.cs.utilities.ArgumentAssert;

public class DataEntityHelperImpl implements DataEntityHelper {

	private final SimpleMappingContext mappingContext;

	public DataEntityHelperImpl(SimpleMappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	@Override
	public boolean isNew(Object entityInstance) {

		ArgumentAssert.notNull(entityInstance, "entityInstance");

		SimplePersistentEntity<?> entity = mappingContext.getPersistentEntity(entityInstance.getClass());
		return entity.isNew(entityInstance);
	}

}
