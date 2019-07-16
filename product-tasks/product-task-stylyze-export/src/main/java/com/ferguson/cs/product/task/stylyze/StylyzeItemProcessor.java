package com.ferguson.cs.product.task.stylyze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.task.stylyze.model.Product;
import com.ferguson.cs.product.task.stylyze.model.ProductCategory;
import com.ferguson.cs.product.task.stylyze.model.ProductGalleryImage;
import com.ferguson.cs.product.task.stylyze.model.ProductRatings;
import com.ferguson.cs.product.task.stylyze.model.ProductSpec;
import com.ferguson.cs.product.task.stylyze.model.ProductVariation;
import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import com.ferguson.cs.product.task.stylyze.model.StylyzeProduct;

public class StylyzeItemProcessor implements ItemProcessor<StylyzeInputProduct, StylyzeProduct> {

	private final ProductService productService;
	private final StylyzeSettings stylyzeSettings;

	StylyzeItemProcessor(ProductService productService, StylyzeSettings stylyzeSettings) {

		this.productService = productService;
		this.stylyzeSettings = stylyzeSettings;
	}

	private String getBreadcrumbs(int categoryId) {
		ProductCategory category = this.productService.getCategory(categoryId);
		if (category == null) {
			return null;
		}
		int parentId = category.getParentId();
		String breadcrumbs = category.getCategoryName();
		while (parentId != 0) {
			ProductCategory parentCategory = this.productService.getCategory(parentId);
			if (parentCategory == null) {
				break;
			}
			breadcrumbs = parentCategory.getCategoryName().concat(">" + breadcrumbs);
			parentId = parentCategory.getParentId();
		}
		return breadcrumbs;
	}

	private String getImageUrl(String manufacturer, String image) {
		if (image == null) {
			return null;
		}
		String manufacturerString = StringUtils.trimAllWhitespace(manufacturer).toLowerCase();
		String imageString = image.replaceAll(" ", "_").toLowerCase();
		return String.format("%s/c_lpad,f_auto,h_1200,t_base/v3/product/%s/%s", stylyzeSettings.getBaseImageUrl(), manufacturerString, imageString);
	}

	@Override
	public StylyzeProduct process(final StylyzeInputProduct inputProduct) throws Exception {
		int familyId = inputProduct.getFamilyId();

		List<Product> databaseProducts = this.productService.getProductData(familyId);
		if (CollectionUtils.isEmpty(databaseProducts)) {
			return null;
		}
		Product product = databaseProducts.get(0);
		StylyzeProduct stylyzeProduct = new StylyzeProduct();
		stylyzeProduct.setIdentifier(product.getFamilyId());

		HashMap<String, Object> metadata = new HashMap<>();
		metadata.put("manufacturer", product.getManufacturer());
		metadata.put("productOverview", product.getDescription());
		metadata.put("breadcrumbs", this.getBreadcrumbs(inputProduct.getCategoryId()));
		metadata.put("productId", product.getProductId());
		metadata.put("title", product.getTitle());
		metadata.put("series", product.getSeries());
		metadata.put("type", product.getType());
		ProductRatings ratings = this.productService.getProductRatings(familyId);

		Float rating = null;
		Integer count = null;
		if (ratings != null) {
			rating = ratings.getRating();
			count = ratings.getCount();
		}
		metadata.put("avgRating", rating);
		metadata.put("numReviews", count);
		stylyzeProduct.setMetadata(metadata);

		// product url
		String slug = String.format("%s %s", product.getManufacturer(), product.getProductId()).replaceAll(" ", "-").toLowerCase();
		stylyzeProduct.setUrl(String.format("%s/%s/s%d", stylyzeSettings.getBaseUrl(), slug, product.getFamilyId()));

		// product images
		List<ProductGalleryImage> galleryImages = this.productService.getProductImages(product.getManufacturer(), product.getProductId());
		if (!CollectionUtils.isEmpty(galleryImages)) {
			List<HashMap<String, String>> images = new ArrayList<>();
			for (ProductGalleryImage galleryImage : galleryImages) {
				HashMap<String, String> image = new HashMap<>();
				image.put("identifier", galleryImage.getImageId());
				image.put("url", this.getImageUrl(product.getManufacturer(), galleryImage.getImage()));
				images.add(image);
			}
			stylyzeProduct.setImages(images);
		}

		// product specs
		List<ProductSpec> productSpecs = this.productService.getProductSpecs(product.getFamilyId(), product.getApplication(), product.getType());
		if (!CollectionUtils.isEmpty(productSpecs)) {
			List<HashMap<String, String>> specs = new ArrayList<>();
			for (ProductSpec productSpec : productSpecs) {
				HashMap<String, String> spec = new HashMap<>();
				spec.put(productSpec.getAttributeName(), productSpec.getValue() + (productSpec.getUnits() != null ? " " + productSpec.getUnits() : ""));
				specs.add(spec);
			}
			stylyzeProduct.setProductSpecs(specs);
		}

		// finishes
		List<HashMap<String, String>> finishes = new ArrayList<>();
		for (Product databaseProduct : databaseProducts) {
			HashMap<String, String> finish = new HashMap<>();
			finish.put("uniqueId", databaseProduct.getUniqueId().toString());
			finish.put("finish", databaseProduct.getFinish());
			finish.put("msrp", databaseProduct.getMsrp());
			Float cost = this.productService.getProductCost(databaseProduct.getUniqueId());
			if (cost != null) {
				finish.put("cost", cost.toString());
			}
			finish.put("sku", databaseProduct.getSku());
			finish.put("upc", databaseProduct.getUpc());
			finish.put("status", databaseProduct.getStatus());
			finish.put("image", this.getImageUrl(databaseProduct.getManufacturer(), databaseProduct.getImage()));
			finishes.add(finish);
		}
		stylyzeProduct.setFinishes(finishes);

		// variations
		List<ProductVariation> productVariations = this.productService.getProductVariations(product.getFamilyId());
		if (!CollectionUtils.isEmpty(productVariations)) {
			List<HashMap<String, Object>> variations = new ArrayList<>();
			for (ProductVariation productVariation : productVariations) {
				List<Product> variationProducts = this.productService.getProductData(productVariation.getFamilyId());
				if (CollectionUtils.isEmpty(variationProducts)) {
					continue;
				}
				HashMap<String, Object> variation = new HashMap<>();
				variation.put("identifier", productVariation.getFamilyId());
				List<Integer> variationFinishes = new ArrayList<>();
				for (Product variationProduct : variationProducts) {
					variationFinishes.add(variationProduct.getUniqueId());
				}
				variation.put("finishes", variationFinishes);
				variations.add(variation);
			}
			stylyzeProduct.setVariations(variations);
		}

		return stylyzeProduct;
	}

}
