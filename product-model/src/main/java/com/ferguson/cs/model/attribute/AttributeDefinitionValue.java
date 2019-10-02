package com.ferguson.cs.model.attribute;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDefinitionValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String value;

	/**
	 * The value that should be displayed in a user interface, this value will be defaulted to the "value" if not specified.
	 *
	 * An example of using this my be have a decimal value of .5 inches and having a display value of "1/2"
	 */
	private String displayValue;

}
