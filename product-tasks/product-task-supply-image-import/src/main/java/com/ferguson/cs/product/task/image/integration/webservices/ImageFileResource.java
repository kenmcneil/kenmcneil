package com.ferguson.cs.product.task.image.integration.webservices;

import org.springframework.core.io.ByteArrayResource;

public class ImageFileResource extends ByteArrayResource {

	private final String filename;

	public ImageFileResource(final byte[] byteArray, final String filename) {
		super(byteArray);
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return filename;
	}
}
