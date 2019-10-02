package com.ferguson.cs.product.api.taxonomy;

import com.ferguson.cs.utilities.ArgumentAssert;

public final class TaxonomyPathUtilities {

	private TaxonomyPathUtilities() {
	}

	/**
	 * This method will extract the taxonomy/root category code from a taxonomy category's path.
	 *
	 * The path is formatted as : <TAXONOMY_CODE>:<CATEGORY_CODE>.<CATEGORY_CODE>....
	 *
	 *   The taxonomy code is always prefixed on the path followed by a colon.
	 *
	 * @param path TaxonomyCategoryPath
	 * @return The taxonomy code.
	 */
	public static String parseTaxonomyCodeFromPath(String path) {
		ArgumentAssert.notNullOrEmpty(path, "path");
		int index = path.indexOf(':');
		if (index == -1) {
			throw new IllegalArgumentException("The path is malformed and is not prefixed with the taxonomy code.");
		}
		return path.substring(0, path.indexOf(':'));
	}
}
