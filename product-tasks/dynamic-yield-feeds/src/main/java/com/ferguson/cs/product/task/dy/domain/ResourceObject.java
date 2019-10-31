package com.ferguson.cs.product.task.dy.domain;

import java.io.File;
import java.io.IOException;

import com.ferguson.cs.task.util.DataFlowTempFileHelper;

/**
 * Helper class to allow for a Resource[] to be leveraged as a bean to be passed
 * around within the job scope
 */
public class ResourceObject {

	private File resource;

	public ResourceObject(String prefix, String suffix) throws IOException {
		resource = DataFlowTempFileHelper.createTempFile(prefix, suffix);
	}

	public File getResource() {
		return resource;
	}

	public void setResourceArray(File resource) {
		this.resource = resource;
	}
}
