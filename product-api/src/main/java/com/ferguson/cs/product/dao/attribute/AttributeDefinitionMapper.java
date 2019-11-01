package com.ferguson.cs.product.dao.attribute;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinitionValue;
import com.ferguson.cs.model.attribute.UnitOfMeasure;

@Mapper
public interface AttributeDefinitionMapper {

	List<UnitOfMeasure> findUnitOfMeasureList(IdCodeCriteria criteria);
    int insertUnitOfMeasure(UnitOfMeasure unitOfMeasure);
    int updateUnitOfMeasure(UnitOfMeasure unitOfMeasure);
    int deleteUnitOfMeasure(UnitOfMeasure unitOfMeasure);


	List<AttributeDefinition> findAttributeDefinitionList(AttributeDefinitionCriteria criteria);
	int insertAttributeDefinition(AttributeDefinition attributeDefinition);
	int updateAttributeDefinition(AttributeDefinition attributeDefinition);
	int deleteAttributeDefinition(AttributeDefinition attributeDefinition);


	int insertAttributeDefinitionValue(@Param("attributeValue") AttributeDefinitionValue attributeValue, @Param("attributeDefinitionId") Integer attributeDefintionId);
	int updateAttributeDefinitionValue(@Param("attributeValue") AttributeDefinitionValue attributeValue, @Param("attributeDefinitionId") Integer attributeDefintionId);
	int deleteAttributeDefinitionValues(@Param("attributeDefinitionId") Integer attributeDefintionId, @Param("attributeValueList") List<AttributeDefinitionValue> exclusionList);

}
