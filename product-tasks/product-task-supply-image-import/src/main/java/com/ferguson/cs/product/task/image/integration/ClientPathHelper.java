package com.ferguson.cs.product.task.image.integration;

import com.ferguson.cs.utilities.ArgumentAssert;

/**
 * Static helper methods for various path needs.
 */
public final class ClientPathHelper {

	private static final char FORWARD_SLASH = '/';

	private ClientPathHelper() {
	}

	/**
	 * Removes leading forward slash(s) characters ("/") on the path value passed.
	 * 
	 * @param pathPart
	 * @return
	 */
	public static String removeLeadingSlash(String pathPart) {
		ArgumentAssert.notNullOrEmpty(pathPart, "pathPart");
		String cleaned = "";
		final String trimmed = pathPart.trim();
		if (!trimmed.startsWith(String.valueOf(ClientPathHelper.FORWARD_SLASH))) {
			return trimmed;
		}
		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (Character.compare(c, ClientPathHelper.FORWARD_SLASH) != 0) {
				cleaned = trimmed.substring(i);
				break;
			}
		}
		return cleaned;
	}

	/**
	 * Appends a forward slash character ("/") on the path value passed.
	 * 
	 * @param pathPart
	 * @return
	 */
	public static String appendTrailingSlash(String pathPart) {
		ArgumentAssert.notNullOrEmpty(pathPart, "pathPart");
		final String trimmed = pathPart.trim();
		if (trimmed.endsWith(String.valueOf(ClientPathHelper.FORWARD_SLASH))) {
			return trimmed;
		}
		return trimmed.concat(String.valueOf(ClientPathHelper.FORWARD_SLASH));
	}

}