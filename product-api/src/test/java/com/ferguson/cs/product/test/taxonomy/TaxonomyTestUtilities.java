package com.ferguson.cs.product.test.taxonomy;

import org.springframework.util.Assert;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.product.dao.taxonomy.TaxonomyDataAccess;
import com.ferguson.cs.utilities.ArgumentAssert;

public class TaxonomyTestUtilities {

	TaxonomyDataAccess taxonomyDataAccess;

	public TaxonomyTestUtilities(TaxonomyDataAccess taxonomyDataAccess) {
		this.taxonomyDataAccess= taxonomyDataAccess;
	}

	public Taxonomy createTaxonomy(String code, String description, TaxonomyCategory rootCategory) {

		Assert.hasText(code, "The code is required to create a taxonomy.");
		Assert.hasText(description, "The description is required to create a taxonomy.");
		Assert.notNull(rootCategory, "The rootCategory is required to create a taxonomy, try using the TestTaxonomyCategoryBuilder.");
		Taxonomy taxonomy = new Taxonomy();
		taxonomy.setCode(code);
		taxonomy.setDescription(description);
		if (rootCategory.getId() == null) {
			rootCategory = insertCategory(rootCategory);
		}
		taxonomy.setRootCategory(new TaxonomyCategoryReference(rootCategory));
		return taxonomyDataAccess.saveTaxonomy(taxonomy);
	}

	public TaxonomyCategory insertCategory(TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "category");
		return taxonomyDataAccess.saveCategory(category);
	}
}
