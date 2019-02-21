package com.ferguson.cs.product.task.stylyze.model;

public class ProductSpec {
    private String productSpecId;
    private String value;
    private String attributeId;
    private String dataType;
    private String attributeName;
    private String showInList;
    private String units;
    private String showInGallery;
    private Boolean hidden;
    private String productFeedInclude;
    private String dictionaryTermId;
    private String shortDescription;
    private String attributeType;

    public String getProductSpecId() {
        return productSpecId;
    }

    public String getValue() {
        return value;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public String getDataType() {
        return dataType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getShowInList() {
        return showInList;
    }

    public String getUnits() {
        return units;
    }

    public String getShowInGallery() {
        return showInGallery;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public String getProductFeedInclude() {
        return productFeedInclude;
    }

    public String getDictionaryTermId() {
        return dictionaryTermId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getAttributeType() {
        return attributeType;
    }
}
