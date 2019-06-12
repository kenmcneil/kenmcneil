package com.ferguson.cs.product.task.image.integration.webservices;

import java.net.URI;
import java.util.List;

public class ProductImageUploadResult {

	private String publicId;
	private String uploadFileName;
	private List<URI> uriList;

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public List<URI> getUriList() {
		return uriList;
	}

	public void setUriList(List<URI> uriList) {
		this.uriList = uriList;
	}

}
