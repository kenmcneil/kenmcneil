package com.ferguson.cs.model.attribute;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.util.Assert;

import com.ferguson.cs.model.Auditable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An attribute provides a way to describe a characteristic about a product, a product variant, product option, or trait within a
 * specific category. The attribute definition defines the "rules" that apply when assigning a value to an attribute and those rules
 * can be applied consistently regardless of where that attribute is linked.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDefinition implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistent ID assigned to the attribute definition.
	 */
	@Id
	private Integer id;

	/**
	 * A unique business key assigned to the attribute definition.
	 *
	 * This value is required.
	 */
	private String code;

	/**
	 * Indicates the datatype for the values of this attribute definition.
	 *
	 * This value is required.
	 */
	private AttributeDatatype datatype;

	/**
	 * A description of the attribute definition.
	 */
	private String description;

	/**
	 * An optional unit of measure to better qualify the numeric value. Examples of unit of measure are : inches, centimeters, pounds, kilometers, etc
	 */
	private UnitOfMeasureReference unitOfMeasure;

	/**
	 * An optional minimum value that can be assigned to the attribute's value.
	 * <p>
	 * This value must be able to be parsed into the datatype defined for the attribute definition.
	 */
	private String minimumValue;

	/**
	 * An optional maximum value that can be assigned to the attribute's value.
	 * <p>
	 * This value must be able to be parsed into the datatype defined for the attribute definition.
	 */
	private String maximumValue;

	/**
	 * If an enumerated value list is defined on an attribute, the values in the list are the ONLY values that can be assigned to an attribute.
	 * <p>
	 * The values in this list must be able to be parsed into the datatype defined for the attribute definition.
	 * <p>
	 *  EXAMPLE, if you define a string attribute definition to represent "shirt color", you can define a finite list of colors of that shirt: "red", "blue", "orange", etc.
	 */
	private List<AttributeDefinitionValue> enumeratedValues;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;

	@Version
	private Integer version;


	public static class AttributeDefinitionBuilder {

		public AttributeDefinitionBuilder unitOfMeasure(UnitOfMeasure value) {
			Assert.notNull(value, "The unit of measure cannot be null");
			unitOfMeasure = new UnitOfMeasureReference(value);
			return this;
		}
		public AttributeDefinitionBuilder unitOfMeasure(UnitOfMeasureReference value) {
			unitOfMeasure = value;
			return this;
		}
		public AttributeDefinitionBuilder enumeratedValue(String value) {
			Assert.notNull(value, "The enumerated value cannot be null.");
			if (enumeratedValues == null) {
				enumeratedValues = new ArrayList<>();
			}
			enumeratedValues.add(new AttributeDefinitionValue(null, value, null));
			return this;
		}
		public AttributeDefinitionBuilder enumeratedValue(Integer value) {
			Assert.notNull(value, "The enumerated value cannot be null.");
			if (enumeratedValues == null) {
				enumeratedValues = new ArrayList<>();
			}
			enumeratedValues.add(new AttributeDefinitionValue(null, value.toString(), null));
			return this;
		}
		public AttributeDefinitionBuilder enumeratedValue(BigDecimal value) {
			Assert.notNull(value, "The enumerated value cannot be null.");
			if (enumeratedValues == null) {
				enumeratedValues = new ArrayList<>();
			}
			enumeratedValues.add(new AttributeDefinitionValue(null, value.toString(), null));
			return this;
		}
		public AttributeDefinitionBuilder minimumValue(String value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value;
			return this;
		}
		public AttributeDefinitionBuilder minimumValue(Integer value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value.toString();
			return this;
		}
		public AttributeDefinitionBuilder minimumValue(BigDecimal value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value.toString();
			return this;
		}
		public AttributeDefinitionBuilder maximumValue(String value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value;
			return this;
		}
		public AttributeDefinitionBuilder maximumValue(Integer value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value.toString();
			return this;
		}
		public AttributeDefinitionBuilder maximumValue(BigDecimal value) {
			Assert.notNull(value, "The minimum value cannot be null.");
			minimumValue =  value.toString();
			return this;
		}

	}
}
