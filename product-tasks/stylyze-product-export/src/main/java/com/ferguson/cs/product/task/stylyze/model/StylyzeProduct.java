package com.ferguson.cs.product.task.stylyze.model;

import java.util.HashMap;
import java.util.List;

public class StylyzeProduct {
    private int identifier;
    private HashMap<String, Object> metadata;
    private String url;
    private List<HashMap<String, String>> images;
    private List<HashMap<String, String>> productSpecs;
    private List<HashMap<String, String>> finishes;
    private List<HashMap<String, Object>> variations;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<HashMap<String, String>> getImages() {
        return images;
    }

    public void setImages(List<HashMap<String, String>> images) {
        this.images = images;
    }

    public List<HashMap<String, String>> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List<HashMap<String, String>> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public List<HashMap<String, String>> getFinishes() {
        return finishes;
    }

    public void setFinishes(List<HashMap<String, String>> finishes) {
        this.finishes = finishes;
    }

    public List<HashMap<String, Object>> getVariations() {
        return variations;
    }

    public void setVariations(List<HashMap<String, Object>> variations) {
        this.variations = variations;
    }
}