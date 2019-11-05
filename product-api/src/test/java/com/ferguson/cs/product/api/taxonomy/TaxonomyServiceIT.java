package com.ferguson.cs.product.api.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static com.ferguson.cs.product.test.GeneralTestUtilities.randomString;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.attribute.AttributeDatatype;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.model.product.ProductReference;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.product.test.BaseProductIT;


public class TaxonomyServiceIT extends BaseProductIT {

	@Autowired
	TaxonomyService taxonomyService;


	@Test
	public void saveTaxonomy_asserts() {

		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveTaxonomy(null)
			);

		//Empty object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveTaxonomy(Taxonomy.builder().build())
			);

		//Null description.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveTaxonomy(Taxonomy.builder().code("MYTAXONOM").build())
			);

		//Try to insert a taxonomy with a pre-defined root category (Not allowed, as it's implicitly created during an insert of the taxonomy.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveTaxonomy(Taxonomy.builder()
						.code("MYTAXONOM")
						.rootCategory(new TaxonomyCategoryReference())
						.build())
			);
	}

	@Test
	public void saveTaxonomy_insert() {
		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		assertThat(taxonomy.getId()).isNotNull();
		assertThat(taxonomy.getVersion()).isEqualTo(1);
		assertThat(taxonomy.getCreatedTimestamp()).isNotNull();
		assertThat(taxonomy.getLastModifiedTimestamp()).isNotNull();

		assertThat(taxonomy.getRootCategory()).isNotNull();
		assertThat(taxonomy.getRootCategory().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(taxonomy.getRootCategory().getDescription()).isEqualTo(taxonomy.getDescription());
		assertThat(taxonomy.getRootCategory().getName()).isEqualTo("ROOT");
		assertThat(taxonomy.getRootCategory().getId()).isNotNull();
		assertThat(taxonomy.getRootCategory().getPath()).isEqualTo("");
	}

	@Test
	public void saveTaxonomy_update() {

		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);
		taxonomy.setDescription("A new Description");
		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		//Make sure the updated description is in the database.
		Optional<Taxonomy> retrievedTaxonomy = taxonomyService.getTaxonomyByCode(taxonomy.getCode());
		assertThat(retrievedTaxonomy).isNotEmpty();
		taxonomy = retrievedTaxonomy.get();
		assertThat(taxonomy.getVersion()).isEqualTo(2);
		assertThat(taxonomy.getDescription()).isEqualTo("A new Description");
		assertThat(taxonomy.getLastModifiedTimestamp()).isNotEqualTo(taxonomy.getCreatedTimestamp());

		//Test optimistic record locking
		final Taxonomy taxonomyOld = taxonomy;
		taxonomyOld.setVersion(1);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> taxonomyService.saveTaxonomy(taxonomyOld)
			);
	}


	@Test
	public void getTaxonomy_byCodeAndId() {

		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		//By code
		Optional<Taxonomy> retrieved = taxonomyService.getTaxonomyByCode(taxonomy.getCode());
		assertThat(retrieved).isNotEmpty();
		taxonomy = retrieved.get();
		assertThat(taxonomy.getId()).isNotNull();
		assertThat(taxonomy.getVersion()).isEqualTo(1);
		assertThat(taxonomy.getCreatedTimestamp()).isNotNull();
		assertThat(taxonomy.getLastModifiedTimestamp()).isNotNull();

		assertThat(taxonomy.getRootCategory()).isNotNull();
		assertThat(taxonomy.getRootCategory().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(taxonomy.getRootCategory().getDescription()).isEqualTo(taxonomy.getDescription());
		assertThat(taxonomy.getRootCategory().getName()).isEqualTo("ROOT");
		assertThat(taxonomy.getRootCategory().getId()).isNotNull();
		assertThat(taxonomy.getRootCategory().getPath()).isEqualTo("");

		//By ID
		retrieved = taxonomyService.getTaxonomyById(taxonomy.getId());
		assertThat(retrieved).isNotEmpty();
		assertThat(taxonomy.getId()).isNotNull();
		assertThat(taxonomy.getVersion()).isEqualTo(1);
		assertThat(taxonomy.getCreatedTimestamp()).isNotNull();
		assertThat(taxonomy.getLastModifiedTimestamp()).isNotNull();

		assertThat(taxonomy.getRootCategory()).isNotNull();
		assertThat(taxonomy.getRootCategory().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(taxonomy.getRootCategory().getDescription()).isEqualTo(taxonomy.getDescription());
		assertThat(taxonomy.getRootCategory().getName()).isEqualTo("ROOT");
		assertThat(taxonomy.getRootCategory().getId()).isNotNull();
		assertThat(taxonomy.getRootCategory().getPath()).isEqualTo("");
	}

	@Test
	public void deleteTaxonomy_simple() {

		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		//Test optimistic record locking
		final Taxonomy deleteCheck = taxonomy;
		deleteCheck.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(deleteCheck)
			);

		//Now test valid delete.
		Optional<Taxonomy> retrieved = taxonomyService.getTaxonomyByCode(taxonomy.getCode());
		assertThat(retrieved).isNotEmpty();
		taxonomyService.deleteTaxonomy(retrieved.get());
		retrieved = taxonomyService.getTaxonomyById(taxonomy.getId());
		assertThat(retrieved).isEmpty();
	}

	@Test
	public void deleteTaxonomy_recursive() {

		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		AttributeDefinition centimeters = insertAttributeDefinitionLengthCentimeters();
		TaxonomyCategory level1_1 = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("Level 1 Element 1")
				.name("Level11")
				.categoryParent(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.product(new ProductReference(1L))
				.build();
		taxonomyService.saveCategory(level1_1);
		TaxonomyCategory level1_2 = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("Level 1 Element 2")
				.name("Level12")
				.categoryParent(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.product(new ProductReference(2L))
				.product(new ProductReference(3L))
				.build();
		taxonomyService.saveCategory(level1_2);
		TaxonomyCategory level2_1_1 = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("Level 2 Element 1 SubElement 1")
				.name("Level211")
				.categoryParent(level1_1)
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.product(new ProductReference(4L))
				.product(new ProductReference(5L))
				.build();
		taxonomyService.saveCategory(level2_1_1);

		//Test optimistic record locking
		final Taxonomy deleteCheck = taxonomy;
		deleteCheck.setVersion(4);
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(deleteCheck)
			);

		//Now test valid delete.
		Optional<Taxonomy> retrieved = taxonomyService.getTaxonomyByCode(taxonomy.getCode());
		assertThat(retrieved).isNotEmpty();
		taxonomyService.deleteTaxonomy(retrieved.get());
		retrieved = taxonomyService.getTaxonomyById(taxonomy.getId());
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

	private AttributeDefinition insertAttributeDefinitionLengthInches() {

		return attributeTestUtilities().insertAttributeDefinition(AttributeDefinition.builder()
				.code(randomString(8))
				.description("Length in Inches")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(insertUnitOfMeasureLengthInches())
				.enumeratedValue(1)
				.enumeratedValue(2)
				.enumeratedValue(3)
				.enumeratedValue(4)
				.enumeratedValue(5)
				.enumeratedValue(6)
				.minimumValue(0)
				.maximumValue(8)
				.build());
	}

	private AttributeDefinition insertAttributeDefinitionLengthCentimeters() {

		return attributeTestUtilities().insertAttributeDefinition(AttributeDefinition.builder()
				.code(randomString(8))
				.description("Length in Inches")
				.datatype(AttributeDatatype.NUMERIC)
				.unitOfMeasure(insertUnitOfMeasureLengthCentimeters())
				.enumeratedValue(9)
				.enumeratedValue(10)
				.enumeratedValue(11)
				.enumeratedValue(12)
				.enumeratedValue(13)
				.enumeratedValue(14)
				.minimumValue(8)
				.maximumValue(15)
				.build());
	}

}
