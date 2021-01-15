package com.ferguson.cs.product.stream.participation.engine.model;

public enum ParticipationContentType {
	PARTICIPATION_V1(1, "participation@1"),
	PARTICIPATION_V2(2, "participation@2"),
	PARTICIPATION_ITEMIZED_V1(3, "participation-itemized@1"),
	PARTICIPATION_ITEMIZED_V2(4, "participation-itemized@2"),
	PARTICIPATION_COUPON_V1(5, "participation-coupon@1");

	private final int contentTypeId;
	private final String nameWithMajorVersion;

	ParticipationContentType(int contentTypeId, String nameWithMajorVersion) {
		this.contentTypeId = contentTypeId;
		this.nameWithMajorVersion = nameWithMajorVersion;
	}

	@Override
	public String toString() {
		return this.nameWithMajorVersion;
	}

	public int contentTypeId() {
		return contentTypeId;
	}

	public String nameWithMajorVersion() {
		return nameWithMajorVersion;
	}

	/**
	 * Get the enum value with the given contentTypeId or null if not present.
	 * If input is null then returns null.
	 */
	public static ParticipationContentType fromContentTypeId(Integer contentTypeId) {
		for (ParticipationContentType v: values()) {
			if (v.contentTypeId ==contentTypeId) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Get the enum value with the given content type or null if not present.
	 * If input is null then returns null.
	 */
	public static ParticipationContentType fromNameWithMajorVersion(String nameWithMajorVersion) {
		for (ParticipationContentType v: values()) {
			if (v.nameWithMajorVersion.equals(nameWithMajorVersion)) {
				return v;
			}
		}
		return null;
	}
}
