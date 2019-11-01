package com.ferguson.cs.model.taxonomy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import com.ferguson.cs.model.Auditable;
import com.ferguson.cs.model.product.Product;
import com.ferguson.cs.model.product.ProductReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A taxonomy category is a grouping of products and sub-categories within a taxonomy's classification system. The categories are organized into a
 * hierarchy of parent/children categories. Each category also defines a list of attributes to better identify the types of products that can be placed
 * into that category. A categories attributes can be marked as "required" or "optional" and a product can only be added to the category if it
 * has all of the required attributes defined in the category.
 *
 * Each attribute within the category is associated with an attribute definition {@link com.ferguson.cs.model.attribute.AttributeDefinition}.  These
 * same definitions are used when defining product attributes and that is how category attributes are mapped to a product's attributes.
 *
 * The category attributes can also be used to derive search facets and the attribute definitions define the datatype and validation rules.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyCategory implements Auditable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistence ID assigned to the category
	 */
	@Id
	private Long id;

	/**
	 * The taxonomy to which this category belongs.
	 */
	private TaxonomyReference taxonomy;

	/**
	 * The unique taxonomy path for this category.
	 */
	private String path;

	/**
	 * A business key assigned to this category that is unique within the context of the taxonomy but is not globally unique.
	 */
	private String code;

	/**
	 * The name of the category ("Lights", "Bathroom", etc)
	 */
	private String name;

	/**
	 * Description of the category
	 */
	private String description;

	/**
	 * The persistent ID of the parent category, top-level categories will NOT have a parent.
	 */
	private TaxonomyCategoryReference categoryParent;

	/**
	 * A list of attributes that are common for products that are assigned to this category.
	 */
	private List<TaxonomyCategoryAttribute> attributes;

	/**
	 * A list of product that have been assigned to this category. A product can only be assigned to this category if
	 * it has all of the category's required traits.
	 */
	private List<ProductReference> products;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;

	@Version
	private Long version;

	public static class TaxonomyCategoryBuilder {

		public TaxonomyCategoryBuilder taxonomy(TaxonomyReference taxonomy) {
			this.taxonomy = taxonomy;
			return this;
		}
		public TaxonomyCategoryBuilder taxonomy(Taxonomy taxonomy) {
			this.taxonomy = new TaxonomyReference(taxonomy);
			return this;
		}
		public TaxonomyCategoryBuilder categoryParent(TaxonomyCategoryReference categoryParent) {
			this.categoryParent = categoryParent;
			return this;
		}
		public TaxonomyCategoryBuilder categoryParent(TaxonomyCategory categoryParent) {
			this.categoryParent = new TaxonomyCategoryReference(categoryParent);
			return this;
		}

		public TaxonomyCategoryBuilder attribute(TaxonomyCategoryAttribute attribute) {
			if (this.attributes == null) {
				this.attributes = new ArrayList<>();
			}
			this.attributes.add(attribute);
			return this;
		}

		public TaxonomyCategoryBuilder product(ProductReference product) {
			if (this.products == null) {
				this.products = new ArrayList<>();
			}
			this.products.add(product);
			return this;
		}
		public TaxonomyCategoryBuilder product(Product product) {
			if (this.products == null) {
				this.products = new ArrayList<>();
			}
			this.products.add(new ProductReference(product));
			return this;
		}
	}
}
