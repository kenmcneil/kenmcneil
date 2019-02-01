package com.ferguson.cs.model.taxonomy;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.ferguson.cs.model.product.Product;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryProductLink {

	String categoryId;

	@DBRef
	Product product;
}
