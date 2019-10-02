package com.ferguson.cs.product.test.taxonomy;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionReference;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;

public class TestTaxonomyCategoryBuilder {

	public static TestTaxonomyCategoryBuilder newTaxonomyCategory() {
		return new TestTaxonomyCategoryBuilder();
	}

	private final TaxonomyCategory category;

	private TestTaxonomyCategoryBuilder() {
		this.category = new TaxonomyCategory();
	}

	public TestTaxonomyCategoryBuilder code(String code) {
		category.setCode(code);
		return this;
	}
	public TestTaxonomyCategoryBuilder description(String description) {
		category.setDescription(description);
		return this;
	}
	public TestTaxonomyCategoryBuilder name(String name) {
		category.setName(name);
		return this;
	}

	public TestTaxonomyCategoryBuilder parent(TaxonomyCategory parent) {
		category.setCategoryParent(new TaxonomyCategoryReference(parent));
		return this;
	}
	public TestTaxonomyCategoryBuilder parent(TaxonomyCategoryReference parent) {
		category.setCategoryParent(parent);
		return this;
	}

	public TestTaxonomyCategoryBuilder attribute(AttributeDefinition definition) {

		TaxonomyCategoryAttribute attribute = new TaxonomyCategoryAttribute();
		attribute.setDefinition(new AttributeDefinitionReference(definition));
		return this;
	}
	public TestTaxonomyCategoryBuilder attribute(AttributeDefinitionReference definition) {

		TaxonomyCategoryAttribute attribute = new TaxonomyCategoryAttribute();
		attribute.setDefinition(definition);
		return this;
	}


//	category.setAttributes();
//	category.setProducts();
//	category.setSubcategories();



}
