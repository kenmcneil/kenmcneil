package com.ferguson.cs.product.api.manufacturer;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferguson.cs.model.manufacturer.Manufacturer;
import com.ferguson.cs.model.manufacturer.ManufacturerCriteria;
import com.ferguson.cs.product.api.lib.OptionalResourceHelper;

@RestController
@RequestMapping("/manufacturers")
public class ManufacturerController {

	private final ManufacturerService manufacturerService;

	public ManufacturerController(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

	@GetMapping(value = "")
	public List<Manufacturer> findManufacturers(@RequestParam("id") Integer id, @RequestParam("name") String name) {
		return manufacturerService.findManufacturers(ManufacturerCriteria.builder()
				.manufacturerId(id)
				.manufacturerName(name)
				.build());
	}

	@GetMapping(value = "/{id}")
	public Manufacturer getManufacturerById(Integer id) {
		return OptionalResourceHelper.handle(manufacturerService.getManufacturerById(id), "Manufacturer",  id);
	}

	@PostMapping(value = "")
	public Manufacturer saveManufacturer(Manufacturer manufacturer) {
		return manufacturerService.saveManufacturer(manufacturer);
	}

	@DeleteMapping(value = "/{code}")
	public void deleteManufacturer(@PathVariable("id") Integer id) {
		Manufacturer manufacturer = getManufacturerById(id);
		manufacturerService.deleteManufacturer(manufacturer);
	}
}
