package com.ferguson.cs.product.task.stylyze.model;

import java.util.HashMap;
import java.util.List;

public class StylyzeProduct {
    private int identifier;
    private HashMap metadata;
    private String url;
    private List images;
    private List productSpecs;
    private List finishes;
    private List variations;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public HashMap getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap metadata) {
        this.metadata = metadata;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List getImages() {
        return images;
    }

    public void setImages(List images) {
        this.images = images;
    }

    public List getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List productSpecs) {
        this.productSpecs = productSpecs;
    }

    public List getFinishes() {
        return finishes;
    }

    public void setFinishes(List finishes) {
        this.finishes = finishes;
    }

    public List getVariations() {
        return variations;
    }

    public void setVariations(List variations) {
        this.variations = variations;
    }
}