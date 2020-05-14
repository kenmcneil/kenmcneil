package com.ferguson.cs.product.stream.participation.engine.model;

public enum ContentErrorMessage implements MessageEnum {

	MISSING_SITE_ID(1, "MISSING_SITE_ID", "The Site ID is required when saving categories"),
	INVALID_CATEGORY(2, "INVALID_CATEGORY", "Unable to find category with Reference ID: %d"),
	INVALID_PRODUCT(3, "INVALID_PRODUCT", "Unable to find product with Reference ID: %d"),
	INVALID_VIDEO(4, "INVALID_VIDEO", "Unable to find video with Reference ID: %d"),
	INVALID_ARTICLE(5, "INVALID_ARTICLE", "Unable to find video with Reference ID: %d"),
	USER_REQUIRED(6, "USER_REQUIRED", "A valid userID is required.  Received [%s]"),
	UNIQUE_CONTENT_REGION_REQUIRED(7, "UNIQUE_CONTENT_REGION_REQUIRED", "A valid unique content region must be passed"),
	INVALID_FAVORITE_LIST(8, "INVALID_FAVORITE_LIST", "Unable to find favorite list with Reference ID: %d"),

	// ContentItem / ContentItemGroup messages
	INVALID_CREATE_CONTENT_ITEM(9, "INVALID_CREATE_CONTENT_ITEM",
			"Content item id must not be set, and group.id must be set"),
	INVALID_CONTENT_ITEM_STATUS(10, "INVALID_CONTENT_ITEM_STATUS",
			"Content item status must not be ACTIVE"),
	INVALID_CONTENT_ITEM_ID(11, "INVALID_CONTENT_ITEM_ID",
			"Content item record not found with id: %d"),
	INVALID_CONTENT_IDENTITY_CHANGE(12, "INVALID_CONTENT_IDENTITY_CHANGE",
			"Content item identity field %s cannot change when updating a record"),
	INVALID_SOURCE_REFERENCE_CONTENT_ITEM(13, "INVALID_SOURCE_REFERENCE_CONTENT_ITEM",
			"The sourceContentItemId must reference a valid content item"),
	INVALID_TARGET_REFERENCE_CONTENT_ITEM(14, "INVALID_TARGET_REFERENCE_CONTENT_ITEM",
			"The targetContentItemId must reference an active content item"),
	INVALID_UPDATE_CONTENT_ITEM(15, "INVALID_UPDATE_CONTENT_ITEM",
			"Content item id must be set"),
	INVALID_CONTENT_ITEM_GROUP_ID(16, "INVALID_CONTENT_ITEM_GROUP_ID",
			"Content item group record not found with id: %s"),
	INVALID_CREATE_CONTENT_ITEM_GROUP(17, "INVALID_CREATE_CONTENT_ITEM_GROUP",
			"Content item group id must not be set unless its an import id (from 1 up to MAX_LEGACY_ARTICLE_ID), and type must be set"),
	INVALID_ACTIVE_UPDATE_CONTENT_ITEM(18, "INVALID_ACTIVE_UPDATE_CONTENT_ITEM",
			"Content item status must be QUICK, and source and target id fields must be set"),
	INVALID_CONTENT_TYPE_ID(19, "INVALID_CONTENT_TYPE_ID",
			"Content type record not found with id: %d"),
	INVALID_UPDATE_CONTENT_TYPE(20, "INVALID_UPDATE_CONTENT_TYPE",
			"Content type id must be set"),
	INVALID_UPDATE_CONTENT_ITEM_GROUP(21, "INVALID_UPDATE_CONTENT_ITEM_GROUP",
			"Content item group must not be null and id must be set"),
	INVALID_CREATE_CONTENT_ITEM_GROUP_DUP_MATCHERS(22, "INVALID_CREATE_CONTENT_ITEM_GROUP_DUP_MATCHERS",
			"Content item group uniqueMatchers must be unique"),
	INVALID_CREATE_EXISTING_CONTENT_ITEM_GROUP(23, "INVALID_CREATE_EXISTING_CONTENT_ITEM_GROUP",
			"Content item group with id %s already exists"),
	INVALID_CONTENT_TYPE_GROUP_ID(24, "INVALID_CONTENT_TYPE_GROUP_ID",
			"Content type group record not found with id: %d"),
	INVALID_UPDATE_CONTENT_TYPE_GROUP(25, "INVALID_UPDATE_CONTENT_TYPE_GROUP",
			"Content type group id must be set"),
	INVALID_PARTIAL_UPDATE_CONTENT_ITEM_GROUP_FIELDS(26, "INVALID_PARTIAL_UPDATE_CONTENT_ITEM_GROUP_FIELDS",
			"There must be at least one field specified to update"),
	INVALID_SOLR_REQUEST_MISSING_ID(27, "INVALID_SOLR_REQUEST_MISSING_ID",
			"An Id must be specified"),
	INVALID_SOLR_REQUEST_MISSING_COLLECTION(28, "INVALID_SOLR_REQUEST_MISSING_COLLECTION",
			"A solr collection must be specified"),
	SOLR_UPDATE_FAILURE(29, "SOLR_UPDATE_FAILURE",
			"Solr failed to update collection %s"),
	INVALID_RESTORE_CONTENT_ITEM(30, "INVALID_RESTORE_CONTENT_ITEM",
			"Content event id must be set, the event must be type DELETE and the item's group must exist"),
	INVALID_CAMPAIGN_ITEM_ID(31, "INVALID_CAMPAIGN_ITEM_ID", "Campaign item record not found with id: %d"),
	INVALID_UPDATE_CAMPAIGN_ITEM(32, "INVALID_UPDATE_CAMPAIGN_ITEM", "Campaign item id must be set"),
	INVALID_CREATE_CAMPAIGN_ITEM(33, "INVALID_CREATE_CAMPAIGN_ITEM", "Campaign item status must be set"),
	INVALID_PARTICIPATION_ITEM_ID(34, "INVALID_PARTICIPATION_ITEM_ID", "Participation item record not found with id: %d"),
	INVALID_UPDATE_PARTICIPATION_ITEM(35, "INVALID_UPDATE_PARTICIPATION_ITEM", "Participation item id must be set"),
	INVALID_UPDATE_PUBLISHED_PARTICIPATION_ITEM(36, "INVALID_UPDATE_PUBLISHED_PARTICIPATION_ITEM", "Quick record must include target and source IDs"),
	INVALID_CREATE_PARTICIPATION_ITEM(37, "INVALID_CREATE_PARTICIPATION_ITEM", "Participation item must not be null and participation item id must not be set"),
	INVALID_PARTICIPATION_ITEM_STATUS_PUBLISHED(38, "INVALID_PARTICIPATION_ITEM_STATUS_PUBLISHED", "Participation item status must not be PUBLISHED"),
	INVALID_SOURCE_REFERENCE_PARTICIPATION_ITEM(39, "INVALID_SOURCE_REFERENCE_PARTICIPATION_ITEM", "Source participation item not found"),
	INVALID_TARGET_REFERENCE_PARTICIPATION_ITEM(40, "INVALID_TARGET_REFERENCE_PARTICIPATION_ITEM", "Published participation item not found"),
	INVALID_PARTICIPATION_ITEM_STATUS(41, "INVALID_PARTICIPATION_ITEM_STATUS", "Participation item status is incorrect for the requested operation"),
	INVALID_PARTIAL_UPDATE_PARTICIPATION_ITEM_FIELDS(42, "INVALID_PARTIAL_UPDATE_PARTICIPATION_ITEM_FIELDS", "There " +
			"must be at least one field specified to update participation item"),
	INVALID_PARTICIPATION_CONTENT_TYPE(43, "INVALID_PARTICIPATION_CONTENT_TYPE",
			"Invalid participation content type"),
	INVAlID_PARTICIPATION_CONTENT(44, "INVAlID_PARTICIPATION_CONTENT", "Unsupported participation content"),
	INVALID_PRODUCT_UNIQUE_ID(45, "INVALID_PRODUCT_UNIQUE_ID", "Invalid product uniqueId found"),
	INVALID_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_ID(46, "INVALID_PARTICIPATION_CALCULATED_DISCOUNT_TEMPLATE_ID", "Invalid or " +
			"missing participation calculated discount template id"),
	UNKNOWN_PUBLISH_PARTICIPATION_ERROR (47, "UNKNOWN_PUBLISH_PARTICIPATION_ERROR", "Unknown Error occurred. " +
			"Unable to publish or publish changes"),
	INVALID_PRICEBOOK_CALCULATED_DISCOUNT_TYPE(48, "INVALID_PRICEBOOK_CALCULATED_DISCOUNT_TYPE",
			"Invalid pricebook calculated discount type");

	private final int code;
    private final String messageTemplate;
    private final String messageType;

    ContentErrorMessage(int code, String messageType, String messageTemplate) {
        this.code = code;
        this.messageType = messageType;
        this.messageTemplate = messageTemplate;
    }

    @Override
	public int getCode() {
        return code;
    }

    @Override
	public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

}
