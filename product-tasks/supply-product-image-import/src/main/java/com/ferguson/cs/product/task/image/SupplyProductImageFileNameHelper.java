package com.ferguson.cs.product.task.image;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.StringUtils;

import com.ferguson.cs.utilities.ArgumentAssert;



public final class SupplyProductImageFileNameHelper {

	private static final String PRODUCT_LEVEL_FAMILY = "FAMILY";
	private static final String PRODUCT_LEVEL_FINISH = "FINISH";
	private static final String FILE_PART_SPLIT = "_";
	private static final String FILE_EXTENTION_PART_SPLIT = "\\.";
	private static final Integer MIN_PARTS = 2;
	private static final Integer MAX_PARTS = 3;
	private static final Integer PRODUCT_NET_SUITE_ID_PART_INDEX = 0;
	private static final Integer PRODUCT_LEVEL_PART_INDEX = 1;
	private static final Integer PRODUCT_EXTENTION_FAMILY_INDEX_OR_PRODUCT_LEVEL_PART_INDEX = 0;
	private static final Integer PRODUCT_EXTENTION_FILE_EXTENTION_PART_INDEX = 1;
	private static final Integer PRODUCT_EXTENTION_ARRAY_SIZE = 2;
	public static final Integer BUSINESS_UNIT_ID_SUPPLY = 3;
	
	public static SupplyProductImageFileName toSupplyProductImageFileName(String fileName) {
		return toSupplyProductImageFileName(fileName, null);
	}

	public static SupplyProductImageFileName toSupplyProductImageFileName(String fileName, String uploadId) {
		ArgumentAssert.notNullOrEmpty(fileName, "fileName");

		// <net-suite-id>_<family|finish>_<family-index>.jpg

		final SupplyProductImageFileName supplyImageFileName = new SupplyProductImageFileName();
		supplyImageFileName.setNameWithExtension(fileName);
		supplyImageFileName.setUploadId(uploadId);

		final String[] fileNameParts = fileName.trim().split(FILE_PART_SPLIT);

		if (fileNameParts.length < MIN_PARTS || fileNameParts.length > MAX_PARTS) {
			supplyImageFileName.getFileNameErrors().add("Invalid name parts. Min or max parts violated.");
			return supplyImageFileName;
		}

		// split last part to get our file extension and family index if applicable.
		final String[] fileNameExtParts = fileNameParts[fileNameParts.length - 1].trim()
				.split(FILE_EXTENTION_PART_SPLIT);

		if (!PRODUCT_EXTENTION_ARRAY_SIZE.equals(fileNameExtParts.length)) {
			supplyImageFileName.getFileNameErrors().add("Invalid name parts. Extension part violated.");
			return supplyImageFileName;
		}

		supplyImageFileName.setFileExtension(fileNameExtParts[PRODUCT_EXTENTION_FILE_EXTENTION_PART_INDEX]);

		supplyImageFileName.setName(
				supplyImageFileName.getNameWithExtension().replace("." + supplyImageFileName.getFileExtension(), ""));

		try {
			supplyImageFileName.setNetSuiteId(Integer.parseInt(fileNameParts[PRODUCT_NET_SUITE_ID_PART_INDEX]));
		} catch (NumberFormatException e) {
			// not a valid supply image file.
			supplyImageFileName.getFileNameErrors().add("Invalid name parts. net-suite-id is invalid.");
			return supplyImageFileName;
		}

		final String productLevel = MIN_PARTS.equals(fileNameParts.length)
				? fileNameExtParts[PRODUCT_EXTENTION_FAMILY_INDEX_OR_PRODUCT_LEVEL_PART_INDEX].toLowerCase()
				: fileNameParts[PRODUCT_LEVEL_PART_INDEX].toLowerCase();

		if (PRODUCT_LEVEL_FAMILY.equalsIgnoreCase(productLevel)
				|| PRODUCT_LEVEL_FINISH.equalsIgnoreCase(productLevel)) {
			supplyImageFileName.setProductLevel(productLevel);
		} else {
			supplyImageFileName.getFileNameErrors().add("Invalid name parts. product-level is invalid.");
			return supplyImageFileName;
		}

		if (Integer.valueOf(MAX_PARTS).equals(fileNameParts.length)) {
			// Will be the first in extension parts, already split from extension.
			try {
				supplyImageFileName.setFamilyIndex(
						Integer.parseInt(fileNameExtParts[PRODUCT_EXTENTION_FAMILY_INDEX_OR_PRODUCT_LEVEL_PART_INDEX]));
			} catch (NumberFormatException e) {
				supplyImageFileName.getFileNameErrors().add("Invalid name parts. family-index is invalid.");
				return supplyImageFileName;
			}
		}

		if (PRODUCT_LEVEL_FAMILY.equalsIgnoreCase(supplyImageFileName.getProductLevel())
				&& supplyImageFileName.getFamilyIndex() == null) {
			supplyImageFileName.getFileNameErrors()
					.add("Invalid name parts. family-index is null for a family level file.");
			return supplyImageFileName;
		}

		if (PRODUCT_LEVEL_FINISH.equalsIgnoreCase(supplyImageFileName.getProductLevel())
				&& supplyImageFileName.getFamilyIndex() != null) {
			supplyImageFileName.getFileNameErrors()
					.add("Invalid name parts. family-index is set for a finish level file.");
			return supplyImageFileName;
		}

		return supplyImageFileName;

	}

	public static class SupplyProductImageFileName {

		private String nameWithExtension;
		private String name;
		private String fileExtension;
		private Integer netSuiteId;
		private String productLevel;
		private Integer familyIndex;
		private String uploadId;
		private List<String> fileNameErrors = new ArrayList<>();

		public Boolean getHasFileNameErrors() {
			return !this.fileNameErrors.isEmpty();
		}

		public List<String> getFileNameErrors() {
			return fileNameErrors;
		}

		public void setFileNameErrors(List<String> fileNameErrors) {
			this.fileNameErrors = fileNameErrors;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNameWithExtension() {
			return nameWithExtension;
		}

		public void setNameWithExtension(String nameWithExtension) {
			this.nameWithExtension = nameWithExtension;
		}

		public Integer getNetSuiteId() {
			return netSuiteId;
		}

		public void setNetSuiteId(Integer netSuiteId) {
			this.netSuiteId = netSuiteId;
		}

		public String getProductLevel() {
			return productLevel;
		}

		public void setProductLevel(String productLevel) {
			this.productLevel = productLevel;
		}

		public Integer getFamilyIndex() {
			return familyIndex;
		}

		public void setFamilyIndex(Integer familyIndex) {
			this.familyIndex = familyIndex;
		}

		public String getUploadId() {
			return uploadId;
		}

		public void setUploadId(String uploadId) {
			this.uploadId = uploadId;
		}

		public String getFileExtension() {
			return fileExtension;
		}

		public void setFileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
		}

		public String getUploadFileName() {
			if (StringUtils.isEmpty(getUploadId()) || StringUtils.isEmpty(getNameWithExtension())) {
				throw new IllegalStateException("Instance must have an uploadId.");
			}
			return getUploadId() + "_" + getNameWithExtension();
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

}
