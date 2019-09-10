package com.ferguson.cs.data;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

public class SimplePersistentEntityImpl<T> extends BasicPersistentEntity<T, SimplePersistentProperty> implements SimplePersistentEntity <T> {
	
	public SimplePersistentEntityImpl(TypeInformation<T> information) {
		super(information);
	}

}
