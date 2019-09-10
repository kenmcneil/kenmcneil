package com.ferguson.cs.product.api.attribute;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.UnitOfMeasure;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;

@RestController
@RequestMapping("/attributes")
public class AttributeController {

	private final AttributeService attributeService;

	public AttributeController(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	@GetMapping(value = "/unit-of-measure/{code}")
	public UnitOfMeasure getUnitOfMeasure(@PathVariable("code") String code) {
		return OptionalResourceHelper.handle(attributeService.getUnitOfMeasureByCode(code), "unit of measure", code);
	}

	//NOTE: It was a conscience choice to merge insert/update into a single call. The use of an insert/update is completely predicated on if the ID field is populated (update) or null (insert)
	@PostMapping(value = "/unit-of-measure")
	public UnitOfMeasure saveUnitOfMeasure(@RequestBody UnitOfMeasure unitOfMeasure) {
		return attributeService.saveUnitOfMeasure(unitOfMeasure);
	}

	@DeleteMapping(value = "/unit-of-measure/{code}")
	public void deleteUnitOfMeasure(@PathVariable("code") String code) {
		UnitOfMeasure unitOfMeasure = getUnitOfMeasure(code);
		attributeService.deleteUnitOfMeasure(unitOfMeasure);
	}

	@GetMapping(value = "/attribute-definition/{code}")
	public AttributeDefinition getAttributeDefinition(@PathVariable("code") String code) {
		return OptionalResourceHelper.handle(attributeService.getAttributeDefinitionByCode(code), "attribute definition", code);
	}

	//NOTE: It was a conscience choice to merge insert/update into a single call. The use of an insert/update is completely predicated on if the ID field is populated (update) or null (insert)
	@PostMapping(value = "/attribute-definition")
	public AttributeDefinition saveAttributeDefinition(@RequestBody AttributeDefinition attributeDefinition) {
		return attributeService.saveAttributeDefinition(attributeDefinition);
	}

	@DeleteMapping(value = "/attribute-definition/{code}")
	public void deleteAttributeDefinition(@PathVariable("code") String code) {
		AttributeDefinition attributeDefinition = getAttributeDefinition(code);
		attributeService.deleteAttributeDefinition(attributeDefinition);
	}

}
