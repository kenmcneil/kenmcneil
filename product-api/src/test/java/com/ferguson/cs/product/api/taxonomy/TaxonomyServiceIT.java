package com.ferguson.cs.product.api.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static com.ferguson.cs.product.test.GeneralTestUtilities.randomString;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import com.ferguson.cs.model.attribute.AttributeDatatype;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;
import com.ferguson.cs.product.test.BaseProductIT;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;


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
						.description("Taxonomy")
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
	public void getTaxonomy_byReference() {

		TaxonomyReference reference = new TaxonomyReference();
		reference.setId(-123);

		//First test when resource does not exist:
		Optional<Taxonomy> retrieved = taxonomyService.getTaxonomyByReference(reference);
		assertThat(retrieved).isEmpty();

		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		taxonomy = taxonomyService.saveTaxonomy(taxonomy);

		//By code
		reference.setId(taxonomy.getId());
		retrieved = taxonomyService.getTaxonomyByReference(reference);
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
	public void deleteTaxonomy_asserts() {
		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(null)
			);

		//Empty object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(Taxonomy.builder()
						.build())
			);

		//Empty version
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(Taxonomy.builder()
						.id(-123541)
						.build())
			);

		//Invalid record
		assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(
				() -> taxonomyService.deleteTaxonomy(Taxonomy.builder()
						.id(-123541)
						.version(1)
						.build())
			);

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
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.build();
		taxonomyService.saveCategory(level1_1);
		TaxonomyCategory level1_2 = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("Level 1 Element 2")
				.name("Level12")
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.build();
		taxonomyService.saveCategory(level1_2);
		TaxonomyCategory level2_1_1 = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("Level 2 Element 1 SubElement 1")
				.name("Level211")
				.parentCategory(level1_1)
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
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

	@Test
	public void getCategoriesByReferences_valid() {
		Taxonomy taxonomy = insertTaxonomy();
		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.build();
		taxonomyService.saveCategory(category);


		Optional<TaxonomyCategory> results = taxonomyService.getCategoryByReference(new TaxonomyCategoryReference(category));

		assertThat(results).isPresent();

		TaxonomyCategory retrieved = results.get();
		assertThat(retrieved.getId()).isEqualTo(category.getId());
		assertThat(retrieved.getCode()).isEqualTo(category.getCode());
		assertThat(retrieved.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
		assertThat(retrieved.getTaxonomy().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(retrieved.getTaxonomy().getDescription()).isEqualTo(taxonomy.getDescription());
		assertThat(retrieved.getDescription()).isEqualTo(category.getDescription());

		assertThat(retrieved.getParentCategory()).isNotNull();
		assertThat(retrieved.getParentCategory().getCode()).isEqualTo(taxonomy.getRootCategory().getCode());
		assertThat(retrieved.getParentCategory().getDescription()).isEqualTo(taxonomy.getRootCategory().getDescription());
		assertThat(retrieved.getParentCategory().getName()).isEqualTo(taxonomy.getRootCategory().getName());
		assertThat(retrieved.getParentCategory().getId()).isEqualTo(taxonomy.getRootCategory().getId());
		assertThat(retrieved.getParentCategory().getPath()).isEqualTo(taxonomy.getRootCategory().getPath());

		assertThat(retrieved.getDescription()).isEqualTo("A category");
		assertThat(retrieved.getName()).isEqualTo("category");
		assertThat(retrieved.getPath()).isEqualTo(category.getCode());
		assertThat(retrieved.getAttributes()).hasSize(1);
		assertThat(retrieved.getAttributes().get(0).getDefinition().getCode()).isEqualTo(inches.getCode());

	}

	@Test
	public void getCategoryById() {
		Taxonomy taxonomy = insertTaxonomy();
		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.build();
		taxonomyService.saveCategory(category);

		Optional<TaxonomyCategory> results = taxonomyService.getCategoryByReference(new TaxonomyCategoryReference(category));

		assertThat(results).isPresent();

		TaxonomyCategory retrieved = results.get();
		assertThat(retrieved.getId()).isEqualTo(category.getId());
		assertThat(retrieved.getCode()).isEqualTo(category.getCode());
		assertThat(retrieved.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
		assertThat(retrieved.getTaxonomy().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(retrieved.getTaxonomy().getDescription()).isEqualTo(taxonomy.getDescription());
		assertThat(retrieved.getDescription()).isEqualTo(category.getDescription());

		assertThat(retrieved.getParentCategory()).isNotNull();
		assertThat(retrieved.getParentCategory().getCode()).isEqualTo(taxonomy.getRootCategory().getCode());
		assertThat(retrieved.getParentCategory().getDescription()).isEqualTo(taxonomy.getRootCategory().getDescription());
		assertThat(retrieved.getParentCategory().getName()).isEqualTo(taxonomy.getRootCategory().getName());
		assertThat(retrieved.getParentCategory().getId()).isEqualTo(taxonomy.getRootCategory().getId());
		assertThat(retrieved.getParentCategory().getPath()).isEqualTo(taxonomy.getRootCategory().getPath());

		assertThat(retrieved.getDescription()).isEqualTo("A category");
		assertThat(retrieved.getName()).isEqualTo("category");
		assertThat(retrieved.getPath()).isEqualTo(category.getCode());
	}

	@Test
	public void findCategoryList_asserts() {

		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.findCategoryList(null)
			);

		//Empty object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
						.build())
			);

		//Path, no taxonomy ID/Code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
						.categoryPath("my.cool.path")
						.build())
			);

		//Taxonomy ID no path.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
						.taxonomyId(1)
						.build())
			);

		//Taxonomy code no path.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
						.taxonomyCode("ONETAXONOMY")
						.build())
			);
	}

	@Test
	public void findCategoryList_byCategoryId() {
		Taxonomy taxonomy = insertTaxonomy();
		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		AttributeDefinition centimeters = insertAttributeDefinitionLengthCentimeters();

		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.build();
		taxonomyService.saveCategory(category);

		List<TaxonomyCategory> results = taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
				.categoryId(category.getId())
				.build());

		assertThat(results).hasSize(1);

		TaxonomyCategory retrieved = results.get(0);
		assertThat(retrieved.getId()).isEqualTo(category.getId());
		assertThat(retrieved.getCode()).isEqualTo(category.getCode());
		assertThat(retrieved.getName()).isEqualTo("category");
		assertThat(retrieved.getPath()).isEqualTo(category.getCode());
		assertThat(retrieved.getDescription()).isEqualTo(category.getDescription());

		assertThat(retrieved.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
		assertThat(retrieved.getTaxonomy().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(retrieved.getTaxonomy().getDescription()).isEqualTo(taxonomy.getDescription());

		assertThat(retrieved.getParentCategory()).isNotNull();
		assertThat(retrieved.getParentCategory().getCode()).isEqualTo(taxonomy.getRootCategory().getCode());
		assertThat(retrieved.getParentCategory().getDescription()).isEqualTo(taxonomy.getRootCategory().getDescription());
		assertThat(retrieved.getParentCategory().getName()).isEqualTo(taxonomy.getRootCategory().getName());
		assertThat(retrieved.getParentCategory().getId()).isEqualTo(taxonomy.getRootCategory().getId());
		assertThat(retrieved.getParentCategory().getPath()).isEqualTo(taxonomy.getRootCategory().getPath());

		assertThat(retrieved.getAttributes()).hasSize(2);
		assertThat(retrieved.getAttributes().get(0).getDefinition().getDescription()).isNotNull();
		assertThat(retrieved.getAttributes().get(0).getDefinition().getUnitOfMeasure().getCode()).isNotNull();
	}

	@Test
	public void findCategoryList_byPath() {
		Taxonomy taxonomy = insertTaxonomy();
		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		AttributeDefinition centimeters = insertAttributeDefinitionLengthCentimeters();

		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.build();
		taxonomyService.saveCategory(category);

		List<TaxonomyCategory> results = taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
				.taxonomyCode(taxonomy.getCode())
				.categoryPath(category.getPath())
				.build());

		assertThat(results).hasSize(1);

		TaxonomyCategory retrieved = results.get(0);
		assertThat(retrieved.getId()).isEqualTo(category.getId());
		assertThat(retrieved.getCode()).isEqualTo(category.getCode());
		assertThat(retrieved.getName()).isEqualTo("category");
	}

	@Test
	public void findCategoryList_byParent() {
		Taxonomy taxonomy = insertTaxonomy();
		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		AttributeDefinition centimeters = insertAttributeDefinitionLengthCentimeters();

		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.build();
		taxonomyService.saveCategory(category);

		List<TaxonomyCategory> results = taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
				.parentCategoryId(taxonomy.getRootCategory().getId())
				.build());

		assertThat(results).hasSize(1);

		TaxonomyCategory retrieved = results.get(0);
		assertThat(retrieved.getId()).isEqualTo(category.getId());
		assertThat(retrieved.getCode()).isEqualTo(category.getCode());
		assertThat(retrieved.getName()).isEqualTo("category");
	}

	@Test
	public void saveCategory_asserts() {

		//Null object
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(null)
			);

		//Empty Name
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(TaxonomyCategory.builder()
						.name("")
						.description("A description")
						.build())
			);

		//Empty Description
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(TaxonomyCategory.builder()
						.name("a name")
						.description("")
						.build())
			);

		//Assertions enforced on insert only.
		TaxonomyCategory category = TaxonomyCategory.builder()
				.code("")
				.description("A category")
				.name("category")
				.build();

		//Empty Code
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		category.setCode("ASDFASDF");
		category.setParentCategory(null);
		//Null parent
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		//Empty Parent
		category.setParentCategory(new TaxonomyCategoryReference(TaxonomyCategory.builder().build()));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		//Null path in parent
		category.setParentCategory(new TaxonomyCategoryReference(TaxonomyCategory.builder().id(-888123L).build()));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		//Null taxonomy
		category.setParentCategory(new TaxonomyCategoryReference(TaxonomyCategory.builder().id(-888123L).path("").build()));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		//Empty taxonomy
		category.setTaxonomy(new TaxonomyReference());
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);

		//Code with a decimal is illegal.
		category.getTaxonomy().setId(1);
		category.setCode("HOLA.DECIMAL");
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);
	}

	@Test
	public void saveCategory_child_parent_path() {
		//Save a parent and then a child,  confirm path is set correctly.
		Taxonomy taxonomy = insertTaxonomy();
		AttributeDefinition inches = insertAttributeDefinitionLengthInches();
		AttributeDefinition centimeters = insertAttributeDefinitionLengthCentimeters();

		TaxonomyCategory parent = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code("PARENT")
				.description("A parent")
				.name("parent")
				.parentCategory(taxonomy.getRootCategory())
				.build();
		taxonomyService.saveCategory(parent);

		TaxonomyCategory child = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code("CHILD")
				.description("Sweet Child of Mine")
				.name("child")
				.attribute(TaxonomyCategoryAttribute.builder().definition(inches).build())
				.attribute(TaxonomyCategoryAttribute.builder().definition(centimeters).build())
				.parentCategory(new TaxonomyCategoryReference(parent))
				.build();
		taxonomyService.saveCategory(child);

		Optional<TaxonomyCategory> results = taxonomyService.getCategoryByReference(new TaxonomyCategoryReference(child));

		assertThat(results).isPresent();

		TaxonomyCategory retrieved = results.get();
		assertThat(retrieved.getId()).isEqualTo(child.getId());
		assertThat(retrieved.getCode()).isEqualTo(child.getCode());
		assertThat(retrieved.getName()).isEqualTo(child.getName());
		assertThat(retrieved.getPath()).isEqualTo("PARENT.CHILD");
		assertThat(retrieved.getTaxonomy().getId()).isEqualTo(taxonomy.getId());
		assertThat(retrieved.getTaxonomy().getCode()).isEqualTo(taxonomy.getCode());
		assertThat(retrieved.getTaxonomy().getDescription()).isEqualTo(taxonomy.getDescription());

		assertThat(retrieved.getParentCategory()).isNotNull();
		assertThat(retrieved.getParentCategory().getCode()).isEqualTo(parent.getCode());
		assertThat(retrieved.getParentCategory().getDescription()).isEqualTo(parent.getDescription());
		assertThat(retrieved.getParentCategory().getName()).isEqualTo(parent.getName());
		assertThat(retrieved.getParentCategory().getId()).isEqualTo(parent.getId());
		assertThat(retrieved.getParentCategory().getPath()).isEqualTo("PARENT");
	}

	@Test
	public void saveCategory_update() {
		Taxonomy taxonomy = insertTaxonomy();
		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.build();
		taxonomyService.saveCategory(category);

		Optional<TaxonomyCategory> results = taxonomyService.getCategoryByReference(new TaxonomyCategoryReference(category));
		assertThat(results).isPresent();
		TaxonomyCategory retrieved= results.get();
		assertThat(retrieved.getVersion()).isEqualTo(1);
		assertThat(retrieved.getCreatedTimestamp()).isNotNull();
		assertThat(retrieved.getLastModifiedTimestamp()).isNotNull();

		retrieved.setDescription("This is a new description");
		taxonomyService.saveCategory(retrieved);

		 results = taxonomyService.getCategoryByReference(new TaxonomyCategoryReference(category));
		 assertThat(results).isPresent();
		 retrieved= results.get();
		assertThat(retrieved.getVersion()).isEqualTo(2);
		assertThat(retrieved.getCreatedTimestamp()).isNotEqualTo(retrieved.getLastModifiedTimestamp());

		//Test optimistic record locking.
		//Empty taxonomy
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> taxonomyService.saveCategory(category)
			);
	}

	@Test
	public void deleteCategory_asserts() {
		//Null object.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteCategory(null)
			);

		//null ID.
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteCategory(TaxonomyCategory.builder().build())
			);

		//null Version
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
				() -> taxonomyService.deleteCategory(TaxonomyCategory.builder().id(123L).build())
			);

	}

	@Test
	public void deleteCategory_optimistic_locking() {
		Taxonomy taxonomy = insertTaxonomy();
		TaxonomyCategory category = TaxonomyCategory.builder()
				.taxonomy(taxonomy)
				.code(randomString(8))
				.description("A category")
				.name("category")
				.parentCategory(taxonomy.getRootCategory())
				.build();
		taxonomyService.saveCategory(category);

		category.setVersion(8);
		//null ID.
		assertThatExceptionOfType(OptimisticLockingFailureException.class).isThrownBy(
				() -> taxonomyService.deleteCategory(category)
			);
	}

	private Taxonomy insertTaxonomy() {
		Taxonomy taxonomy = Taxonomy.builder()
				.code(randomString(8))
				.description("My Cool Taxonomy")
				.build();

		return taxonomyService.saveTaxonomy(taxonomy);
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
