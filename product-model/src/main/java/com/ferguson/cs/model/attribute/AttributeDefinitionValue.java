package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class AttributeDefinitionValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String value;

}
