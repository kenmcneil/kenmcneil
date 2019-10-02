package com.ferguson.cs.product.dao.taxonomy;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.product.ProductReference;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;

@Mapper
public interface TaxonomyMapper {

	List<Taxonomy> findTaxonomies(IdCodeCriteria criteria);
	int insertTaxonomy(Taxonomy taxonomy);

	/**
	 * Note, the description is the only field that may be updated once a taxonomy has been created. This method will update the auditing and version columns.
	 * @param taxonomy The taxonomy that will be updated.
	 * @return The updated version of the taxonomy.
	 */
	int updateTaxonomy(Taxonomy taxonomy);
	int deleteTaxonomy(Taxonomy taxonomy);

	List<TaxonomyCategory> findCategories(TaxonomyCategoryCriteria criteria);
	int insertCategory(TaxonomyCategory category);
	int updateCategory(TaxonomyCategory category);
	int deleteCategory(TaxonomyCategory category);
	void linkTaxonomyRootCategory(Taxonomy savedTaxonomy);

	int insertCategoryAttribute(@Param("attribute") TaxonomyCategoryAttribute attribute, @Param("category") TaxonomyCategory category);
	int updateCategoryAttribute(@Param("attribute") TaxonomyCategoryAttribute attribute, @Param("category") TaxonomyCategory category);
	void deleteCatagoryAttributes(TaxonomyCategory category, List<TaxonomyCategoryAttribute> exclusionList);

	int insertCategoryProductReference(@Param("product") ProductReference product, @Param("category") TaxonomyCategory category);
	int updateCategoryProductReference(@Param("product") ProductReference product, @Param("category") TaxonomyCategory category);
	void deleteCategoryProductReferences(TaxonomyCategory category, List<ProductReference> exclusionList);


}
