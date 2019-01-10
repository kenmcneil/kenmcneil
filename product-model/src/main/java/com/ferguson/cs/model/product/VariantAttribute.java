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
public class VariantAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String variantId;
	private AttributeDefinition<?> definition;
	private String value;

}
