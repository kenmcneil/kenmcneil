package com.ferguson.cs.product.api.taxonomy;

import java.util.List;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;
import com.ferguson.cs.utilities.ArgumentAssert;

@RestController
@RequestMapping("/taxonomies")
public class TaxonomyController {

	private final TaxonomyService taxonomyService;
	public TaxonomyController(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	@GetMapping(value = "/{taxonomyCode}")
	public Taxonomy getTaxonomy(@PathVariable("taxonomyCode") String code) {
		return OptionalResourceHelper.handle(taxonomyService.getTaxonomyByCode(code), "taxonomy", code);
	}

	@PostMapping(value = "")
	public Taxonomy insertTaxonomy(@RequestBody Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		Assert.isNull(taxonomy.getVersion(), "You may only use this method to insert a new taxonomy.");
		return taxonomyService.saveTaxonomy(taxonomy);
	}

	@PutMapping(value = "taxonomyCode")
	public Taxonomy updateTaxonomy(@PathVariable("taxonomyCode") String code, @RequestBody Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		Assert.notNull(taxonomy.getId(), "You may only use this method to update an existing taxonomy.");
		Assert.notNull(taxonomy.getVersion(), "You may only use this method to update an existing taxonomy.");
		Assert.isTrue(code.equals(taxonomy.getCode()), "The taxonomy has a different code then the one specified in the path.");
		return taxonomyService.saveTaxonomy(taxonomy);
	}

	@DeleteMapping(value = "/{taxonomyCode}")
	public void deleteTaxonomy(@PathVariable("taxonomyCode") String code) {
		Taxonomy taxonomy = getTaxonomy(code);
		taxonomyService.deleteTaxonomy(taxonomy);
	}

	@GetMapping(value = "{taxonomyCode}/path/{path}")
	public TaxonomyCategory getCategoryByPath(@PathVariable("taxonomyCode") String  taxonomyCode, @PathVariable("path") String  path) {

		List<TaxonomyCategory> results = taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
				.taxonomyCode(taxonomyCode)
				.categoryPath(path)
				.build());

		if (results.isEmpty()) {
			throw new ResourceNotFoundException("The taxonomy [" + taxonomyCode + "] does not have a category associated with the path [" + path + "].");
		}
		return results.get(0);
	}

	@GetMapping(value = "{taxonomyCode}/path/{path}/subCategories")
	public List<TaxonomyCategory> getSubCategoriesByPath(@PathVariable("taxonomyCode") String  taxonomyCode, @PathVariable("path") String  path) {

		TaxonomyCategory parentCategory = getCategoryByPath(taxonomyCode, path);

		return taxonomyService.findCategoryList(TaxonomyCategoryCriteria.builder()
				.categoryIdParent(parentCategory.getId())
				.build()
			);
	}

	@GetMapping(value = "/{taxonomyCode}/categories/{categoryId}")
	public TaxonomyCategory getCategory(@PathVariable("taxonomyCode") String taxonomyCode, Long categoryId) {
		return OptionalResourceHelper.handle(taxonomyService.getCategoryById(categoryId), "taxonomy category", "" + categoryId);
	}

	@PostMapping(value = "/{taxonomyCode}/categories")
	public TaxonomyCategory insertCategory(@PathVariable("taxonomyCode") String taxonomyCode, @RequestBody TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "category");
		Assert.isNull(category.getVersion(), "You may only use this method to insert a new taxonomy.");
		String categoryTaxonomyCode = category.getTaxonomy() == null?null:category.getTaxonomy().getCode();
		Assert.isTrue(taxonomyCode.equals(categoryTaxonomyCode), "The taxonomy [" + categoryTaxonomyCode + "] does not match the taxonomy [" + taxonomyCode + "] specified in the path.");

		return taxonomyService.saveCategory(category);
	}

	@PutMapping(value = "/taxonomyCode/categories/{categoryId}")
	public TaxonomyCategory updateCategory(
			@PathVariable("taxonomyCode") String taxonomyCode,
			@PathVariable("categoryId") Long categoryId,
			@RequestBody TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "taxonomy");
		Assert.notNull(category.getId(), "You may only use this method to update an existing taxonomy.");
		Assert.isTrue(category.getId().equals(categoryId), "The category ID [" + category.getId() + "] does not match the category ID [" + categoryId+ "] specified in the path.");
		Assert.notNull(category.getVersion(), "You may only use this method to update an existing taxonomy.");
		String categoryTaxonomyCode = category.getTaxonomy() == null?null:category.getTaxonomy().getCode();
		Assert.isTrue(taxonomyCode.equals(categoryTaxonomyCode), "The taxonomy [" + categoryTaxonomyCode + "] does not match the taxonomy [" + taxonomyCode + "] specified in the path.");

		return taxonomyService.saveCategory(category);
	}

	@DeleteMapping(value = "{taxonomyCode}/categories/{categoryId}")
	public void deleteTaxonomyCategory(@PathVariable("taxonomyCode") String  taxonomyCode, Long categoryId) {

		Optional<TaxonomyCategory> category = taxonomyService.getCategoryById(categoryId);
		if (!category.isPresent()) {
			throw new ResourceNotFoundException("The taxonomy category [" + categoryId + "]  was not found.");
		}
		taxonomyService.deleteCategory(category.get());
	}



}
