package com.ferguson.cs.product.task.image.integration.webservices;

import java.net.URI;

public class ProductImageUploadResult {

	private String publicId;
	private String uploadFileName;
	private URI uri;

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

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

}
