package com.ferguson.cs.product.api.taxonomy;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;

@RestController
@RequestMapping("/taxonomies")
public class TaxonomyController {

	private final TaxonomyService taxonomyService;
	public TaxonomyController(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	@GetMapping(value = "/{code}")
	public Taxonomy getTaxonomy(@PathVariable("code") String code) {
		return OptionalResourceHelper.handle(taxonomyService.getTaxonomyByCode(code), "taxonomy", code);
	}

	@PostMapping(value = "")
	public Taxonomy saveTaxonomy(@RequestBody Taxonomy taxonomy) {
		return taxonomyService.saveTaxonomy(taxonomy);
	}

	@DeleteMapping(value = "/{code}")
	public void deleteTaxonomy(@PathVariable("code") String code) {
		Taxonomy taxonomy = getTaxonomy(code);
		taxonomyService.deleteTaxonomy(taxonomy);
	}
}
