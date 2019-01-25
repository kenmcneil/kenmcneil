package com.ferguson.cs.productapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.productapi.attribute.AttributeService;

@RestController
@RequestMapping("/attributes")
public class AttributeController {

	private final AttributeService attributeService;

	public AttributeController(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	@GetMapping(value = "/unit-of-measure/{code}")
	public UnitOfMeasure getUnitOfMeasure(@PathVariable("code") String uomCode) {
		return attributeService.getUnitOfMeasure(uomCode);
	}

	//NOTE: It was a conscience choice to merge insert/update into a single call. The use of an insert/update is completely predicated on if the ID field is populated (update) or null (insert)
	@PostMapping(value = "/unit-of-measure")
	public UnitOfMeasure saveUnitOfMeasure(@RequestBody UnitOfMeasure unitOfMeasure) {
		return attributeService.saveUnitOfMeasure(unitOfMeasure);
	}

	@DeleteMapping(value = "/unit-of-measure/{code}")
	public void deleteArticle(@PathVariable("code") String uomCode) {
		attributeService.deleteUnitOfMeasure(uomCode);
	}

}
