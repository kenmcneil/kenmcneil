package com.ferguson.cs.product.api.attribute;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static com.ferguson.cs.product.test.GeneralTestUtilities.randomString;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDatatype;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.model.attribute.UnitOfMeasureReference;
import com.ferguson.cs.product.test.BaseProductIT;

public class AttributeServiceIT extends BaseProductIT {

	@Autowired
	AttributeService attributeService;

	@Test
	public void saveUnitOfMeasure_asserts() {
		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(null)
			);
		//Null code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(
						UnitOfMeasure.builder().description("This is a description").build())
			);
		//Empty code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(
						UnitOfMeasure.builder()
							.code("")
							.description("This is a description")
							.build()
					)
			);
		//Null description
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(
						UnitOfMeasure.builder().code("123").build())
			);
		//Empty code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(
						UnitOfMeasure.builder()
							.code("123")
							.description("")
							.build()
					)
			);
	}

	@Test
	public void saveUnitOfMeasure_insert() {

		UnitOfMeasure uom = UnitOfMeasure.builder()
				.code(randomString(8))
				.description("This is a Test")
				.name("Test in Inches")
				.build();

		uom = attributeService.saveUnitOfMeasure(uom);

		assertThat(uom.getId()).isNotNull();
		assertThat(uom.getVersion()).isEqualTo(1);
		assertThat(uom.getCreatedTimestamp()).isNotNull();
		assertThat(uom.getLastModifiedTimestamp()).isNotNull();

		//Test duplicate key :
		final UnitOfMeasure uom2 = uom;
		uom2.setId(null);
		assertThatExceptionOfType(DataAccessException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(uom2)
			);

	}

	@Test
	public void saveUnitOfMeasure_update() {

		UnitOfMeasure uom = UnitOfMeasure.builder()
				.code(randomString(8))
				.description("This is a Test")
				.name("Test in Inches")
				.build();

		uom = attributeService.saveUnitOfMeasure(uom);
		uom.setDescription("A new Description");
		uom = attributeService.saveUnitOfMeasure(uom);

		//Make sure the updated description is in the database.
		Optional<UnitOfMeasure> retrievedUom = attributeService.getUnitOfMeasureByCode(uom.getCode());
		assertThat(retrievedUom).isNotEmpty();
		uom = retrievedUom.get();
		assertThat(uom.getVersion()).isEqualTo(2);
		assertThat(uom.getDescription()).isEqualTo("A new Description");
		assertThat(uom.getLastModifiedTimestamp()).isNotEqualTo(uom.getCreatedTimestamp());

		//Test optimistic record locking
		final UnitOfMeasure uomOld = uom;
		uomOld.setVersion(1);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> attributeService.saveUnitOfMeasure(uomOld)
			);
	}

	@Test
	public void getUnitOfMeasure_byCodeAndId() {

		UnitOfMeasure uom = UnitOfMeasure.builder()
				.code(randomString(8))
				.description("This is a Test")
				.name("Test in Inches")
				.build();

		uom = attributeService.saveUnitOfMeasure(uom);

		//By code
		Optional<UnitOfMeasure> retrievedUom = attributeService.getUnitOfMeasureByCode(uom.getCode());
		assertThat(retrievedUom).isNotEmpty();
		uom = retrievedUom.get();
		assertThat(uom.getVersion()).isEqualTo(1);
		assertThat(uom.getDescription()).isEqualTo("This is a Test");

		//By ID
		retrievedUom = attributeService.getUnitOfMeasureById(uom.getId());
		assertThat(retrievedUom).isNotEmpty();
		uom = retrievedUom.get();
		assertThat(uom.getVersion()).isEqualTo(1);
		assertThat(uom.getDescription()).isEqualTo("This is a Test");
	}

	@Test
	public void findUnitOfMeasure() {

		UnitOfMeasure uomInches = insertUnitOfMeasureLengthInches();
		UnitOfMeasure uomCentimeters = insertUnitOfMeasureLengthCentimeters();
		List<UnitOfMeasure> results = attributeService.findUnitsOfMeasure(null);

		assertThat(results.size()).isGreaterThan(1);

		//Make sure all fields are populated.
		assertThat(results.get(0).getId()).isNotNull();
		assertThat(results.get(0).getCode()).isNotBlank();
		assertThat(results.get(0).getDescription()).isNotBlank();
		assertThat(results.get(0).getCreatedTimestamp()).isNotNull();
		assertThat(results.get(0).getLastModifiedTimestamp()).isNotNull();
		assertThat(results.get(0).getVersion()).isNotNull();

		//Make sure both test uom records are in the results.
		assertThat(results).anyMatch((uom) -> {
				return uom.getId().equals(uomInches.getId());
			});
		assertThat(results).anyMatch((uom) -> {
				return uom.getId().equals(uomCentimeters.getId());
			});

		//Test for a specific Id.
		results = attributeService.findUnitsOfMeasure(IdCodeCriteria.builder()
					.id(uomInches.getId())
					.build()
			);
		assertThat(results.size()).isEqualTo(1);
		assertThat(results).anyMatch((uom) -> {
			return uom.getId().equals(uomInches.getId());
		});

		//Test for a specific code.
		results = attributeService.findUnitsOfMeasure(IdCodeCriteria.builder()
					.code(uomCentimeters.getCode())
					.build()
			);
		assertThat(results.size()).isEqualTo(1);
		assertThat(results).anyMatch((uom) -> {
			return uom.getId().equals(uomCentimeters.getId());
		});
	}

	@Test
	public void deleteUnitOfMeasure() {
		UnitOfMeasure uom = UnitOfMeasure.builder()
				.code(randomString(8))
				.description("This is a Test")
				.name("Test in Inches")
				.build();

		uom = attributeService.saveUnitOfMeasure(uom);

		//Test optimistic record locking
		final UnitOfMeasure uomOld = uom;
		uomOld.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> attributeService.deleteUnitOfMeasure(uomOld)
			);

		//Now test valid delete.
		Optional<UnitOfMeasure> retrievedUom = attributeService.getUnitOfMeasureByCode(uom.getCode());
		assertThat(retrievedUom).isNotEmpty();
		attributeService.deleteUnitOfMeasure(retrievedUom.get());
		retrievedUom = attributeService.getUnitOfMeasureByCode(uom.getCode());
		assertThat(retrievedUom).isEmpty();
	}

	@Test
	public void saveAttributeDefinition_asserts() {
		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(null)
			);
		//Null code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder().description("This is a description").build())
			);
		//Empty code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder()
							.code("")
							.description("This is a description")
							.build()
					)
			);
		//Null description
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder().code("123").build())
			);
		//Empty code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder()
							.code("123")
							.description("")
							.build()
					)
			);

		//null UOM ID
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder()
							.unitOfMeasure(new UnitOfMeasureReference(UnitOfMeasure.builder().build()))
							.description("123")
							.code("123")
							.build()
					)
			);
		//Invalid UOM
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(
						AttributeDefinition.builder()
							.unitOfMeasure(new UnitOfMeasureReference(UnitOfMeasure.builder().id(-1723).build()))
							.description("123")
							.code("123")
							.build()
					)
			);
	}

	@Test
	public void saveAttributeDefinition_insert() {
		UnitOfMeasure uom = insertUnitOfMeasureLengthInches();

		AttributeDefinition definition = AttributeDefinition.builder()
				.code(randomString(8))
				.description("Cool attribute definition")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(new UnitOfMeasureReference(uom))
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.enumeratedValue(5)
				.enumeratedValue(6)
				.minimumValue(0)
				.maximumValue(8)
				.build();
		definition = attributeService.saveAttributeDefinition(definition);

		assertThat(definition.getId()).isNotNull();
		assertThat(definition.getVersion()).isEqualTo(1);
		assertThat(definition.getCreatedTimestamp()).isNotNull();
		assertThat(definition.getLastModifiedTimestamp()).isNotNull();

		//Test duplicate key :
		final AttributeDefinition definitionDupCheck= definition;
		definitionDupCheck.setId(null);
		assertThatExceptionOfType(DataAccessException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(definitionDupCheck)
			);

	}

	@Test
	public void saveAttributeDefinition_update() {

		UnitOfMeasure uomInches = insertUnitOfMeasureLengthInches();
		UnitOfMeasure uomCentimeters = insertUnitOfMeasureLengthCentimeters();

		AttributeDefinition definition = AttributeDefinition.builder()
				.code(randomString(8))
				.description("Cool attribute definition")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(uomInches)
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.enumeratedValue(5)
				.enumeratedValue(6)
				.minimumValue(0)
				.maximumValue(8)
				.build();

		definition = attributeService.saveAttributeDefinition(definition);
		definition.setDescription("A new description");
		definition.setUnitOfMeasure(new UnitOfMeasureReference(uomCentimeters));
		definition.getEnumeratedValues().remove(0);
		definition.getEnumeratedValues().remove(0);

		definition = attributeService.saveAttributeDefinition(definition);

		//Make sure the definition in the database matches the changes made.
		Optional<AttributeDefinition> retrieved = attributeService.getAttributeDefinitionByCode(definition.getCode());
		assertThat(retrieved).isNotEmpty();
		definition = retrieved.get();
		assertThat(definition.getVersion()).isEqualTo(2);
		assertThat(definition.getDescription()).isEqualTo("A new description");
		assertThat(definition.getEnumeratedValues()).hasSize(4);
		assertThat(definition.getUnitOfMeasure().getCode()).isEqualTo(uomCentimeters.getCode());
		assertThat(definition.getLastModifiedTimestamp()).isNotEqualTo(definition.getCreatedTimestamp());

		//Test optimistic record locking
		final AttributeDefinition old  = definition;
		old.setVersion(1);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> attributeService.saveAttributeDefinition(old)
			);
	}

	@Test
	public void getAttributeDefinition_byCodeAndId() {

		UnitOfMeasure uomInches = insertUnitOfMeasureLengthInches();

		AttributeDefinition definition = AttributeDefinition.builder()
				.code(randomString(8))
				.description("Cool attribute definition")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(uomInches)
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.enumeratedValue(5)
				.enumeratedValue(6)
				.minimumValue(0)
				.maximumValue(8)
				.build();

		definition = attributeService.saveAttributeDefinition(definition);

		//By code
		Optional<AttributeDefinition> retrieved = attributeService.getAttributeDefinitionByCode(definition.getCode());
		assertThat(retrieved).isNotEmpty();
		definition = retrieved.get();
		assertThat(definition.getDescription()).isEqualTo("Cool attribute definition");
		assertThat(definition.getDatatype()).isEqualTo(AttributeDatatype.NUMERIC);
		assertThat(definition.getEnumeratedValues()).hasSize(6);
		assertThat(definition.getUnitOfMeasure().getCode()).isEqualTo(uomInches.getCode());
		assertThat(definition.getVersion()).isEqualTo(1);
		assertThat(definition.getCreatedTimestamp()).isNotNull();
		assertThat(definition.getLastModifiedTimestamp()).isNotNull();

		//By ID
		retrieved = attributeService.getAttributeDefinitionById(definition.getId());
		assertThat(retrieved).isNotEmpty();
		definition = retrieved.get();
		assertThat(definition.getDescription()).isEqualTo("Cool attribute definition");
		assertThat(definition.getDatatype()).isEqualTo(AttributeDatatype.NUMERIC);
		assertThat(definition.getEnumeratedValues()).hasSize(6);
		assertThat(definition.getUnitOfMeasure().getCode()).isEqualTo(uomInches.getCode());
		assertThat(definition.getVersion()).isEqualTo(1);
		assertThat(definition.getCreatedTimestamp()).isNotNull();
		assertThat(definition.getLastModifiedTimestamp()).isNotNull();
	}

	@Test
	public void findAttributeDefintions() {

		//Insert two definitions
		UnitOfMeasure uomInches = insertUnitOfMeasureLengthInches();
		AttributeDefinition definition1 = AttributeDefinition.builder()
				.code(randomString(8))
				.description("IT ATRB TESTING - LENGTH INCHES")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(uomInches)
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.minimumValue(0)
				.maximumValue(8)
				.build();
		AttributeDefinition definition2 = AttributeDefinition.builder()
				.code(randomString(8))
				.description("IT ATRB TESTING - COLOR")
				.datatype(AttributeDatatype.STRING)
				.enumeratedValue("blue")
				.enumeratedValue("green")
				.enumeratedValue("red")
				.build();

		definition1 = attributeService.saveAttributeDefinition(definition1);
		definition2 = attributeService.saveAttributeDefinition(definition2);

		//First search by description (This test assumes there will ONLY be the two testing records).
		List<AttributeDefinition> results = attributeService.findAttributeDefinitions(AttributeDefinitionCriteria.builder()
				.attributeDefinitionDescription("IT ATRB TESTING%")
				.build()
			);

		assertThat(results).hasSize(2);
		for (AttributeDefinition definition : results) {
			if (definition.getId().equals(definition1.getId())) {
				assertThat(definition.getCode()).isEqualTo(definition1.getCode());
				assertThat(definition.getDescription()).isEqualTo(definition1.getDescription());
				assertThat(definition.getDatatype()).isEqualTo(AttributeDatatype.NUMERIC);
				assertThat(definition.getEnumeratedValues()).containsExactlyInAnyOrder(
					definition1.getEnumeratedValues().get(0),
					definition1.getEnumeratedValues().get(1),
					definition1.getEnumeratedValues().get(2),
					definition1.getEnumeratedValues().get(3)
					);
				assertThat(definition.getCreatedTimestamp()).isNotNull();
				assertThat(definition.getLastModifiedTimestamp()).isNotNull();
				assertThat(definition.getVersion()).isNotNull();
			} else {
				assertThat(definition.getCode()).isEqualTo(definition2.getCode());
				assertThat(definition.getDescription()).isEqualTo(definition2.getDescription());
				assertThat(definition.getDatatype()).isEqualTo(AttributeDatatype.STRING);
				assertThat(definition.getEnumeratedValues()).containsExactlyInAnyOrder(
					definition2.getEnumeratedValues().get(0),
					definition2.getEnumeratedValues().get(1),
					definition2.getEnumeratedValues().get(2));
				assertThat(definition.getCreatedTimestamp()).isNotNull();
				assertThat(definition.getLastModifiedTimestamp()).isNotNull();
				assertThat(definition.getVersion()).isNotNull();
			}
		}

		//Test for a specific Id and unit of measure
		results = attributeService.findAttributeDefinitions(AttributeDefinitionCriteria.builder()
				.attributeDefinitionId(definition1.getId())
				.unitOfMeasureCode(uomInches.getCode())
				.build()
			);
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getCode()).isEqualTo(definition1.getCode());

		//Test for a specific code.
		results = attributeService.findAttributeDefinitions(AttributeDefinitionCriteria.builder()
				.attributeDefinitionCode(definition2.getCode())
				.build()
			);
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getId()).isEqualTo(definition2.getId());

	}

	@Test
	public void deleteAttributeDefinition() {
		UnitOfMeasure uomInches = insertUnitOfMeasureLengthInches();
		AttributeDefinition original = AttributeDefinition.builder()
				.code(randomString(8))
				.description("IT ATRB TESTING - LENGTH INCHES")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(uomInches)
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.minimumValue(0)
				.maximumValue(8)
				.build();

		original = attributeService.saveAttributeDefinition(original);

		//Test optimistic record locking
		final AttributeDefinition deleteCheck = original;
		deleteCheck.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> attributeService.deleteAttributeDefinition(deleteCheck)
			);

		//Now test valid delete.
		Optional<AttributeDefinition> retrieved = attributeService.getAttributeDefinitionByCode(original.getCode());
		assertThat(retrieved).isNotEmpty();
		attributeService.deleteAttributeDefinition(retrieved.get());
		retrieved = attributeService.getAttributeDefinitionByCode(original.getCode());
		assertThat(retrieved).isEmpty();
	}

	/**
	 * Created test unit of measure for length in inches.
	 */
	private UnitOfMeasure insertUnitOfMeasureLengthInches() {

		return attributeTestUtilities().insertUnitOfMeasure(
				UnitOfMeasure.builder()
				.code(randomString(8))
				.description("Length In Inches")
				.name("Lenght (Inches)")
				.build());
	}
	/**
	 * Created test unit of measure for length in inches.
	 */
	private UnitOfMeasure insertUnitOfMeasureLengthCentimeters() {

		return attributeTestUtilities().insertUnitOfMeasure(
				UnitOfMeasure.builder()
				.code(randomString(8))
				.description("Length In Centimeters")
				.name("Lenght (Centimeters)")
				.build());
	}
}
