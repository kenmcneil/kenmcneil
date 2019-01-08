package com.ferguson.cs.model.attribute;

import java.util.Arrays;
import java.util.List;

public class BooleanAttributeDefinition extends AttributeDefinition<Boolean> {

	@Override
	public List<Boolean> getEnumeratedValueList() {
		return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
	}
}
