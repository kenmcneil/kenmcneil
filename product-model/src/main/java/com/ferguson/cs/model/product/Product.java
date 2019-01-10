package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.model.image.ImageResource;
import com.ferguson.cs.model.manufacturer.Manufacturer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String manufacturerIdentifier;
	private String title;
	private String description;	
	private Manufacturer manufacturer;
	private ProductSeries series;
	private ProductCollectionType collectionType;
	private List<ProductAttribute> attributeList;
	private List<Variant> variantList;
	private List<ImageResource> imageList;

	//TODO Need to figure out what to do with auditing columns (timestampCreated, timestampUpdated)
	
}
