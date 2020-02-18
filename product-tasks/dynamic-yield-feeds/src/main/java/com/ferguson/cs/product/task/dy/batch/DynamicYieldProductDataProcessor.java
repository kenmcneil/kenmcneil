package com.ferguson.cs.product.task.dy.batch;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.task.dy.domain.CommonConfig;
import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;
import com.ferguson.cs.product.task.dy.model.ProductData;

public class DynamicYieldProductDataProcessor implements ItemProcessor<ProductData, DynamicYieldProduct> {

	private static final String IMAGE_URL_STRING = "https://s3.img-b.com/image/private/c_lpad,f_auto,h_220,t_base,w_220/v3/product/";
	private static final String STOCK_STATUS_STRING = "stock";
	private static final String DISCONTINUED_STATUS_STRING = "discontinued";
	private static final String NO_IMAGE_REGEX = "(?i:.*noimage.jpg)";
	private static final String RELATIVE_PATH_STRING = "v3/product/";
	private static final String WHITE_SPACE_STRING = " ";

	@Override
	public DynamicYieldProduct process(ProductData item) throws Exception {
		DynamicYieldProduct dyProduct = new DynamicYieldProduct();

		if (isValidAndIncluded(item)) {
			dyProduct.setSku(item.getSku());
			dyProduct.setGroupId(item.getGroupId());
			dyProduct.setName(item.getName());
			dyProduct.setPrice(item.getPrice());
			dyProduct.setModel(item.getModel());
			dyProduct.setManufacturer(item.getManufacturer());
			dyProduct.setSeries(item.getSeries());
			dyProduct.setTheme(item.getTheme());
			dyProduct.setGenre(item.getGenre());
			dyProduct.setFinish(item.getFinish());
			dyProduct.setRating(item.getRating());
			dyProduct.setType(item.getType());
			dyProduct.setApplication(item.getApplication());
			dyProduct.setHandletype(item.getHandletype());
			dyProduct.setMasterfinish(item.getMasterfinish());
			dyProduct.setMountingType(item.getMountingType());
			dyProduct.setInstallationType(item.getInstallationType());
			dyProduct.setNumberOfBasins(item.getNumberOfBasins());
			dyProduct.setNominalLength(item.getNominalLength());
			dyProduct.setNominalWidth(item.getNominalWidth());
			dyProduct.setNumberOfLights(item.getNumberOfLights());
			dyProduct.setChandelierType(item.getChandelierType());
			dyProduct.setPendantType(item.getPendantType());
			dyProduct.setFanType(item.getFanType());
			dyProduct.setFuelType(item.getFuelType());
			dyProduct.setConfiguration(item.getConfiguration());
			dyProduct.setCaliforniaDroughtCompliant(item.getCaliforniaDroughtCompliant());
			dyProduct.setBaseCategory(item.getBaseCategory());
			dyProduct.setBusinessCategory(item.getBusinessCategory());

			dyProduct.setSiteIds(Arrays.asList(item.getSiteIds().split(",")).stream()
					.map(x -> Integer.parseInt(x)).collect(Collectors.toList()));

			dyProduct.setUrl(CommonConfig.PRODUCT_URL_REPLACEMENT + ":" + dyProduct.getSku() + ":" + dyProduct.getGroupId());
			dyProduct.setInStock(item.getStatus().equalsIgnoreCase(STOCK_STATUS_STRING));
			dyProduct.setImageUrl(IMAGE_URL_STRING + item.getManufacturer().replaceAll(WHITE_SPACE_STRING, "") + '/'
					+ item.getImage());
			dyProduct.setHasImage(item.getImage().matches(NO_IMAGE_REGEX));
			dyProduct.setCategories(item.getType() + '|' + item.getApplication());
			dyProduct.setDiscontinued(item.getStatus().equalsIgnoreCase(DISCONTINUED_STATUS_STRING));
			dyProduct.setRelativePath(RELATIVE_PATH_STRING + item.getManufacturer().replaceAll(WHITE_SPACE_STRING, "") + '/'
					+ item.getImage());

			if (StringUtils.hasText(item.getHandletype())) {
				dyProduct.setCategories(dyProduct.getCategories() + '|' + item.getHandletype());
			}
		} else {
			return null;
		}

		return dyProduct;
	}

	/**
	 * Check all required fields for data
	 *
	 * @param productData
	 * @return true or false
	 */
	private boolean isValidAndIncluded(ProductData productData) {
		return (productData != null
				&& productData.getSku() != null
				&& productData.getGroupId() != null
				&& productData.getStatus() != null
				&& StringUtils.hasText(productData.getName())
				&& StringUtils.hasText(productData.getManufacturer())
				&& StringUtils.hasText(productData.getImage())
				&& productData.getPrice() != null
				&& StringUtils.hasText(productData.getApplication())
				&& StringUtils.hasText(productData.getType()));
	}
}
