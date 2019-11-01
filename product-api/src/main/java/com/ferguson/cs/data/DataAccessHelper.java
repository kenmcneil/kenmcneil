package com.ferguson.cs.data;

import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * This help class is meant to help standardize the handling of persistence operations. This class leverages the spring-data-commons
 * library to determine if an entity is new, populating auditing column, and optimistic record locking.
 *<P><P>
 * This helper is narrowly scoped to be leveraged the spring-data annotation within the entity classes:
 * <P><P>
 * <li>{@link Id} can be used to denote which field is mapped to the persistent key.</li>
 * <li>{@link Version} can be used on aggregate roots to enforce optimistic record locking.</li>
 * <li>{@link CreatedDate} and {@link LastModifiedDate} can be used to populate auditing timestamps each time the entity is saved.</li>
 *  <P><P>
 *  This helper also assumes that the actual database queries will be delegated to via Mybatis mapper calls.
 *
 * @author tyler.vangorder
 *
 */
public interface DataAccessHelper extends DataEntityHelper {

	/**
	 * This method handles the delete operation of an entity and will delegate to a mapper's delete function. If a version property is present on the entity,
	 * it will handle optimistic record locking.
	 * <P><P>
	 * To correctly support versioning and optimistic record locking, the mapper method for the delete must return the number of rows impacted by the
	 * delete and the delete must have the form:
	 *
	 * <PRE>delete from ... where id=#{id} and version=#{version}</PRE>
	 *
	 * @param <T> Then entity type.
	 * @param entityInstance The instance that will be deleted.
	 * @param deleteFunction A function reference to a Mybatis mapper statement that will issue the delete against the database.
	 * @throws OptimisticLockingFailureException If the entity has a version property and 0 rows are returned from the mapper function.
	 */
	<T> void deleteEntity(T entityInstance, ToIntFunction<T> deleteFunction);

	/**
	 * This method handles the insert/update of an entity and will use spring-data common's annotations to determine if the entity is "new", populate auditing fields, and will handle optimistic
	 * record locking.
	 * <P><P>
	 * 	<li>This method determines if the entity is "new" using the same logic as {@link DataAccessHelper#isNew(Object)}. This will dictate which method is called insert/update</li>
	 * 	<li>If the entity has property annotated with {@link CreatedDate}, this method will populate this value on insert with the current timestamp.</li>
	 * 	<li>If the entity has property annotated with {@link LastModifiedDate}, this method will populate this value (on insert or update) with the current timestamp.</li>
	 * 	<li>If the entity has a numeric property annotated with {@link Version}, this method will populate this value with "1" on insert or "version + 1" on a successful save.
	 *
	 * To correctly support versioning and optimistic record locking:
	 *
	 * The data access helper does NOT set the version property until AFTER the save, this requires that the insert and update statements must have the follow form:
	 *
	 * <PRE>insert into ... (....., version) values (....., 1)</PRE>
	 * <PRE>update  ... set (....., version_column=version_column+1) where id=#{id} and version_column=#{version}</PRE>
	 *
	 *
	 * @param <T> Then entity type.
	 * @param entityInstance The instance that will be deleted.
	 * @param insertFunction A function reference to a Mybatis mapper statement that will issue the insert against the database.
	 * @param updateFunction A function reference to a Mybatis mapper statement that will issue the update against the database.
	 * @throws OptimisticLockingFailureException If the entity has a version property and 0 rows are returned from the update function.
	 * @return The updated entity with its ID, auditing, and version columns all updated.
	 */
	<T> T saveEntity(T entityInstance, ToIntFunction<T> insertFunction, ToIntFunction<T> updateFunction);

	/**
	 * This method handles the insert/update of an child entity and will use spring-data common's annotations to determine if the entity is "new", populate auditing fields, and will handle optimistic
	 * record locking. The parent is used to populate the FK to the parent record in the database.
	 * <P><P>
	 * 	<li>This method determines if the entity is "new" using the same logic as {@link DataAccessHelper#isNew(Object)}. This will dictate which method is called insert/update</li>
	 * 	<li>If the entity has property annotated with {@link CreatedDate}, this method will populate this value on insert with the current timestamp.</li>
	 * 	<li>If the entity has property annotated with {@link LastModifiedDate}, this method will populate this value (on insert or update) with the current timestamp.</li>
	 * 	<li>If the entity has a numeric property annotated with {@link Version}, this method will populate this value with "1" on insert or "version + 1" on a successful save.
	 *
	 * To correctly support versioning and optimistic record locking:
	 *
	 * The data access helper does NOT set the version property until AFTER the save, this requires that the insert and update statements must have the follow form:
	 *
	 * <PRE>insert into ... (....., version) values (....., 1)</PRE>
	 * <PRE>update  ... set (....., version_column=version_column+1) where id=#{id} and version_column=#{version}</PRE>
	 *
	 * @param <Child> The child entity instance
	 * @param <Parent> The parent entity instance
	 * @param insertFunction A function reference to a Mybatis mapper statement that will issue the insert against the database.
	 * @param updateFunction A function reference to a Mybatis mapper statement that will issue the update against the database.
	 * @throws OptimisticLockingFailureException If the entity has a version property and 0 rows are returned from the update function.
	 * @return The updated entity with its ID, auditing, and version columns all updated.
	 */
	<C, P> C saveChildEntity(C childEntityInstance, P parentEntityInstance,  BiFunction<C, P, Integer> insertFunction, BiFunction<C, P, Integer> updateFunction);
}
