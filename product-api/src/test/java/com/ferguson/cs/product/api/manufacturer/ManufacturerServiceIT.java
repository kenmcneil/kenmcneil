package com.ferguson.cs.product.api.manufacturer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.product.test.BaseProductIT;

public class ManufacturerServiceIT extends BaseProductIT {

	@Autowired
	ManufacturerService manufacturerService;

	@Test
	public void saveManufacturer_asserts() {
		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> manufacturerService.saveManufacturer(null)
			);
		//Null name
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> manufacturerService.saveManufacturer(Manufacturer.builder()
						.build())
			);

		//Null description
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> manufacturerService.saveManufacturer(Manufacturer.builder()
						.name("Toad Inc")
						.build())
			);
	}

	@Test
	public void saveManufacturer_insert() {

		Manufacturer manufacturer = Manufacturer.builder()
				.name("Toad Inc")
				.description("Maker of fine amphibian products")
				.build();

		manufacturer = manufacturerService.saveManufacturer(manufacturer);

		assertThat(manufacturer.getId()).isNotNull();
		assertThat(manufacturer.getVersion()).isEqualTo(1);
		assertThat(manufacturer.getCreatedTimestamp()).isNotNull();
		assertThat(manufacturer.getLastModifiedTimestamp()).isNotNull();
		assertThat(manufacturer.getName()).isEqualTo("Toad Inc");

		//Test duplicate name :
		final Manufacturer manufacturer2 = manufacturer;
		manufacturer2.setId(null);
		assertThatExceptionOfType(DataAccessException.class).isThrownBy(
				() -> manufacturerService.saveManufacturer(manufacturer2)
			);
	}

	@Test
	public void saveManufacturer_update() {

		Manufacturer manufacturer = Manufacturer.builder()
				.name("Toad Inc")
				.description("Maker of fine amphibian products")
				.build();

		manufacturer = manufacturerService.saveManufacturer(manufacturer);

		assertThat(manufacturer.getId()).isNotNull();
		assertThat(manufacturer.getVersion()).isEqualTo(1);
		assertThat(manufacturer.getCreatedTimestamp()).isNotNull();
		assertThat(manufacturer.getLastModifiedTimestamp()).isNotNull();
		assertThat(manufacturer.getName()).isEqualTo("Toad Inc");
		manufacturer.setDescription("Slightly used amphibian products.");
		manufacturer = manufacturerService.saveManufacturer(manufacturer);

		//Make sure the updated description is in the database.
		Optional<Manufacturer> retrievedManufacturer = manufacturerService.getManufacturerById(manufacturer.getId());
		assertThat(retrievedManufacturer).isNotEmpty();
		manufacturer = retrievedManufacturer.get();
		assertThat(manufacturer.getVersion()).isEqualTo(2);
		assertThat(manufacturer.getDescription()).isEqualTo("Slightly used amphibian products.");
		assertThat(manufacturer.getLastModifiedTimestamp()).isNotEqualTo(manufacturer.getCreatedTimestamp());

		//Test optimistic record locking
		final Manufacturer manufacturerOld = manufacturer;
		manufacturerOld.setVersion(1);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> manufacturerService.saveManufacturer(manufacturerOld)
			);
	}

	@Test
	public void getManufacturerById() {

		Manufacturer manufacturer = Manufacturer.builder()
				.name("Toad Inc")
				.description("Maker of fine amphibian products")
				.build();

		manufacturer = manufacturerService.saveManufacturer(manufacturer);

		Optional<Manufacturer> retrievedManufacturer = manufacturerService.getManufacturerById(manufacturer.getId());
		assertThat(retrievedManufacturer).isNotEmpty();

		manufacturer = retrievedManufacturer.get();
		assertThat(manufacturer.getVersion()).isEqualTo(1);
		assertThat(manufacturer.getDescription()).isEqualTo("Maker of fine amphibian products");
	}


	@Test
	public void deleteManufacturer() {
		Manufacturer manufacturer = Manufacturer.builder()
				.name("Toad Inc")
				.description("Maker of fine amphibian products")
				.build();

		manufacturer = manufacturerService.saveManufacturer(manufacturer);

		//Test optimistic record locking
		final Manufacturer manufacturerOld = manufacturer;
		manufacturerOld.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> manufacturerService.deleteManufacturer(manufacturerOld)
			);

		//Now test valid delete.
		Optional<Manufacturer> retrievedManufacturer = manufacturerService.getManufacturerById(manufacturer.getId());
		assertThat(retrievedManufacturer).isNotEmpty();
		manufacturerService.deleteManufacturer(retrievedManufacturer.get());
		retrievedManufacturer = manufacturerService.getManufacturerById(manufacturer.getId());
		assertThat(retrievedManufacturer).isEmpty();
	}

}
