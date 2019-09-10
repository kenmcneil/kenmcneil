package com.ferguson.cs.data;

import java.util.function.ToIntFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * This abstract can be extended and provides methods that leverage the annotations provides by spring-data-commons to
 * standardize the persistence behavior for determining if the entity is new, handling auditing, and optimistic record locking.
 * @author tyler.vangorder
 */
@Repository
public abstract class AbstractDataAccess {
	
	@Autowired
	private DataAccessHelper dataAccessHelper;
		
	protected DataAccessHelper getDataAccessHelper() {
		return dataAccessHelper;
	}

	protected boolean isNew(Object entityInstance) {
		return dataAccessHelper.isNew(entityInstance);
	}
	protected 	<T> void deleteEntity(T entityInstance, ToIntFunction<T> deleteFunction) {
		dataAccessHelper.deleteEntity(entityInstance, deleteFunction);
	}
	protected <T> T saveEntity(T entityInstance, ToIntFunction<T> insertFunction, ToIntFunction<T> updateFunction) {
		return dataAccessHelper.saveEntity(entityInstance, insertFunction, updateFunction);
	}	
}
