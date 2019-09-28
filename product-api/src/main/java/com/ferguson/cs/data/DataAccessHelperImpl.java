package com.ferguson.cs.data;

import java.util.function.ToIntFunction;

import org.springframework.core.convert.ConversionService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;

import com.ferguson.cs.utilities.ArgumentAssert;

public class DataAccessHelperImpl implements DataAccessHelper {

	private final SimpleMappingContext mappingContext;
	private final  AuditingHandler auditingHandler;
	private final ConversionService conversionService;
	
	public DataAccessHelperImpl(SimpleMappingContext mappingContext,  AuditingHandler auditingHandler, ConversionService conversionService) {
		this.mappingContext = mappingContext;
		this.auditingHandler = auditingHandler;
		this.conversionService = conversionService;
	}

	@Override
	public boolean isNew(Object entityInstance) {

		ArgumentAssert.notNull(entityInstance, "entityInstance");
		
		SimplePersistentEntity<?> entity = mappingContext.getPersistentEntity(entityInstance.getClass());
		return entity.isNew(entityInstance);
	}

	@Override
	public <T> void deleteEntity(T entityInstance, ToIntFunction<T> deleteFunction) {
		ArgumentAssert.notNull(entityInstance, "entityInstance");

		SimplePersistentEntity<T> entity = getPersistentEntity(entityInstance);
		
		int rowsDeleted = deleteFunction.applyAsInt(entityInstance);
		if (entity.hasVersionProperty() && rowsDeleted == 0) {
			throw new OptimisticLockingFailureException(String.format("Optimistic lock exception while deleting entity of type %s", entity.getName()));
		}
	}

	@Override
	public <T> T saveEntity(T entityInstance, ToIntFunction<T> insertFunction, ToIntFunction<T> updateFunction) {
		ArgumentAssert.notNull(entityInstance, "entityInstance");
		SimplePersistentEntity<T> entity = getPersistentEntity(entityInstance);
		boolean isNew = entity.isNew(entityInstance);
		boolean isVersioned = entity.hasVersionProperty();

		if (isNew) {
			auditingHandler.markCreated(entityInstance);
			insertFunction.applyAsInt(entityInstance);
			if (isVersioned) {
				entityInstance = setVersionOnInstance(entityInstance, 1, entity);
			}
		} else {
			auditingHandler.markModified(entityInstance);
			int rowsUpdated = updateFunction.applyAsInt(entityInstance);
			if (isVersioned && rowsUpdated  == 1) {
				Number version = getVersionFromInstance(entityInstance, entity);
				entityInstance = setVersionOnInstance(entityInstance, version.longValue()+ 1, entity);				
			} else if(isVersioned) {
				throw new OptimisticLockingFailureException(String.format("Optimistic lock exception while saving entity of type %s", entity.getName()));								
			}
		}
			
		return entityInstance;		
	}
	
	@SuppressWarnings("unchecked")
	private <T> SimplePersistentEntity<T> getPersistentEntity(T entityInstance) {
		return (SimplePersistentEntity<T>) mappingContext.getPersistentEntity(entityInstance.getClass());
	}
	
	private <T> Number getVersionFromInstance(T entityInstance, SimplePersistentEntity<T> entity) {
		ConvertingPropertyAccessor<T> propertyAccessor = getConvertingPropertyAccessor(entityInstance, entity);
		return propertyAccessor.getProperty(entity.getRequiredVersionProperty(), Number.class);
	}
	private <T> T setVersionOnInstance(T entityInstance, Number version, SimplePersistentEntity<T> entity) {
		ConvertingPropertyAccessor<T> propertyAccessor = getConvertingPropertyAccessor(entityInstance, entity);
		SimplePersistentProperty versionProperty = entity.getRequiredVersionProperty();
		propertyAccessor.setProperty(versionProperty, version);
		return propertyAccessor.getBean();
	}

	private <T> ConvertingPropertyAccessor<T> getConvertingPropertyAccessor(T instance, SimplePersistentEntity<T> persistentEntity) {
		return new ConvertingPropertyAccessor<>(persistentEntity.getPropertyAccessor(instance), conversionService);		
	}

}
