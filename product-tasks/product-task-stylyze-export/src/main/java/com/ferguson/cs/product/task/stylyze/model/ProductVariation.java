package com.ferguson.cs.product.task.stylyze.model;

public class ProductVariation {

	private Integer id;
	private String name;
	private Integer familyId;
	private Integer dictionaryTermId;
	private String variationName;
	private Integer sortOrder;
	private String image;
	private Integer useProductImage;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Integer familyId) {
		this.familyId = familyId;
	}

	public Integer getDictionaryTermId() {
		return dictionaryTermId;
	}

	public void setDictionaryTermId(Integer dictionaryTermId) {
		this.dictionaryTermId = dictionaryTermId;
	}

	public String getVariationName() {
		return variationName;
	}

	public void setVariationName(String variationName) {
		this.variationName = variationName;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getUseProductImage() {
		return useProductImage;
	}

	public void setUseProductImage(Integer useProductImage) {
		this.useProductImage = useProductImage;
	}

}
