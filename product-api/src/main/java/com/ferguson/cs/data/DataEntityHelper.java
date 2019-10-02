package com.ferguson.cs.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;

/**
 * This helper can be used in the service layer to leverage spring data's commons library to do reflective work on the entities.
 *
 * @author tyler.vangorder
 */
public interface DataEntityHelper {

	/**
	 * This method will delegate to spring-data's isNewStrategy interface to determine if the entity has/has not been previously saved.
	 * <P><P>
	 * The current logic is used to determine if an entity is new:
	 * <P><P>
	 * <li>if the entity implements {@link Persistable} the isNew is delegated to the entity.</li>
	 * <li>If the entity has a {@link Version} property and that value is null, the entity is considered "new" and non-null value is considered "existing."</li>
	 * <li>If the entity has an {@link Id} proeprty and that value is null, the entity is considered "new" and non-null is considered "existing"</li>
	 * @param entityInstance The entity that will be tested to determine if it represents a new or an existing record.
	 * @return True if the entity has not been saved to the underlying database.
	 */
	boolean isNew(Object entityInstance);

}
