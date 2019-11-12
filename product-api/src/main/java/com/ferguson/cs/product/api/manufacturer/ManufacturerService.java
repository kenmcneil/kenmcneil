package com.ferguson.cs.product.api.manufacturer;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.model.manufacturer.ManufacturerCriteria;

public interface ManufacturerService {

	/**
	 * Find a list of manufacturers that match the criteria passed into this method.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>id</B></TD><TD>Limit the manufacturer search to a specific ID</TD></TR>
	 * <TR><TD><B>name</B></TD><TD>Limit the manufacturer search to a name pattern. Wild cards are allowed</TD></TR>
	 * </TABLE>
	 *
	 * @param criteria The criteria to search by.
	 * @return A list of matching manufacturers or an empty list if there are not matching manufacturers.
	 */
	List<Manufacturer> findManufacturers(ManufacturerCriteria criteria);

	/**
	 * Retrieve a specific manufacturer by its persistent ID.
	 * @param id The manufacturer's ID.
	 * @return The manufacturer.
	 */
	Optional<Manufacturer> getManufacturerById(Integer id);

	/**
	 * Save a manufacturer record to the underlying database. This method will perform an insert or an update depending on
	 * if the manufacturer is determined to be "new".
	 * <P><P>
	 * This method will enforce optimistic record locking and populate the auditing columns.
	 *
	 * @param manufacturer The manufacturer that will be saved to the underlying data store.
	 * @return The manufacturer record with any modifications that have been made as part of the save operation.
	 * @throws OptimisticLockingFailureException if the save operation would result in a dirty write.
	 * @throws IllegalArgumentException If the required properties are not supplied.
	 */
	Manufacturer saveManufacturer(Manufacturer manufacturer);

	/**
	 * Delete a manufacturer record from the underlying database.
	 * <P><P>
	 * This method will enforce optimistic record locking and will not allow a delete operation if the record has been modified
	 * and does not reflect the current state of the manufacturer being passed in.
	 *
	 * @param manufacturer The manufacturer to delete
	 * @throws OptimisticLockingFailureException if the delete operation would result in a dirty write.
	 * @throws IllegalArgumentException If the required properties are not supplied.
	 */
	void deleteManufacturer(Manufacturer manufacturer);
}
