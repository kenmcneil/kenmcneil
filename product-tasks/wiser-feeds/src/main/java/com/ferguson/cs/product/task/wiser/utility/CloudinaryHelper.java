package com.ferguson.cs.product.task.wiser.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CloudinaryHelper {
	private static final Integer DEFAULT_HEIGHT = 320;
	private static final Integer DEFAULT_WIDTH = 320;
	private static final String CLOUDINARY_PREFIX = "https://s3.img-b.com/image/private/";
	private static final String CHAR_SET = StandardCharsets.UTF_8.displayName();

	private CloudinaryHelper() { }


	/**
	 * Creates a url to a cloudinary image
	 *
	 * @param manufacturerId	manufacturer
	 * @param imageUrl	url to the non-cloudinary image
	 * @return cloudinary image url
	 */
	public static String createCloudinaryProductUrl(String manufacturerId, String imageUrl) {
		return createCloudinaryProductUrl(manufacturerId, imageUrl, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	private static String createCloudinaryProductUrl(String manufacturerId, String imageUrl, Integer width, Integer height) {
		StringBuilder sb = new StringBuilder(CLOUDINARY_PREFIX);
		sb.append("c_lpad,f_auto,h_").append(height).append(",t_base,w_").append(width);
		sb.append("/product/");

		try {
			sb.append(URLEncoder.encode(manufacturerId.replaceAll("\\s", ""), CHAR_SET));
		} catch (UnsupportedEncodingException e) {
			sb.append(manufacturerId.replaceAll("\\s", ""));
		}

		sb.append("/");

		try {
			sb.append(URLEncoder.encode(imageUrl, CHAR_SET));
		} catch (UnsupportedEncodingException e) {
			sb.append(imageUrl);
		}

		return sb.toString();
	}
}
