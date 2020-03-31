package com.ferguson.cs.product.task.dy.utility;

import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;

public final class DynamicYieldHelper {
    public static final DynamicYieldProduct initializeDynamicYieldProduct(DynamicYieldProduct product, Integer siteId) {
        DynamicYieldProduct dyProduct = new DynamicYieldProduct();

        dyProduct.setSku(product.getSku());
        dyProduct.setGroupId(product.getGroupId());
        dyProduct.setName(product.getName());
        dyProduct.setPrice(product.getPrice());
        dyProduct.setModel(product.getModel());
        dyProduct.setManufacturer(product.getManufacturer());
        dyProduct.setSeries(product.getSeries());
        dyProduct.setTheme(product.getTheme());
        dyProduct.setGenre(product.getGenre());
        dyProduct.setFinish(product.getFinish());
        dyProduct.setRating(product.getRating());
        dyProduct.setType(product.getType());
        dyProduct.setApplication(product.getApplication());
        dyProduct.setHandletype(product.getHandletype());
        dyProduct.setMasterfinish(product.getMasterfinish());
        dyProduct.setMountingType(product.getMountingType());
        dyProduct.setInstallationType(product.getInstallationType());
        dyProduct.setNumberOfBasins(product.getNumberOfBasins());
        dyProduct.setNominalLength(product.getNominalLength());
        dyProduct.setNominalWidth(product.getNominalWidth());
        dyProduct.setNumberOfLights(product.getNumberOfLights());
        dyProduct.setChandelierType(product.getChandelierType());
        dyProduct.setPendantType(product.getPendantType());
        dyProduct.setFanType(product.getFanType());
        dyProduct.setFuelType(product.getFuelType());
        dyProduct.setConfiguration(product.getConfiguration());
        dyProduct.setCaliforniaDroughtCompliant(product.getCaliforniaDroughtCompliant());
        dyProduct.setBaseCategory(product.getBaseCategory());
        dyProduct.setBusinessCategory(product.getBusinessCategory());
        dyProduct.setSiteIds(product.getSiteIds());
        dyProduct.setUrl(product.getUrl());
        dyProduct.setInStock(product.getInStock());
        dyProduct.setImageUrl(product.getImageUrl());
        dyProduct.setHasImage(product.getHasImage());
        dyProduct.setCategories(product.getCategories());
        dyProduct.setDiscontinued(product.getDiscontinued());
        dyProduct.setRelativePath(product.getRelativePath());
        if (product.getCategoryNameSiteMap().get(siteId) != null) {
            dyProduct.setKeywords(String.join("|", product.getCategoryNameSiteMap().get(siteId)));
        }

        return dyProduct;
    }
}
