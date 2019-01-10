package com.ferguson.cs.model.product;

import java.io.Serializable;

import com.ferguson.cs.model.attribute.AttributeDefinition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String productId;
	private AttributeDefinition<?> definition;
	private boolean overrideAllowed;
	private String value;

}
