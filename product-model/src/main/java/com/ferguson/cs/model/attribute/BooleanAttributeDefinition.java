package com.ferguson.cs.model.attribute;

import java.util.Arrays;
import java.util.List;

import lombok.ToString;

@ToString(callSuper=true)
public class BooleanAttributeDefinition extends AttributeDefinition<Boolean> {

	private static final long serialVersionUID = 1L;

	@Override
	final public List<Boolean> getEnumeratedValueList() {
		return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
	}
}
