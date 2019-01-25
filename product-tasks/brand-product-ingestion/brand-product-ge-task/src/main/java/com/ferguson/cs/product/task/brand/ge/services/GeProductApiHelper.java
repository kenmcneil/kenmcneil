package com.ferguson.cs.product.task.brand.ge.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.task.brand.ge.aws.AwsVersion4RequestSigner;
import com.ferguson.cs.product.task.brand.model.JsonReference;
import com.ferguson.cs.product.task.brand.model.JsonType;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.utilities.DateUtils;
import com.ferguson.cs.utilities.StringUtils;
import com.ge_products.api.GeProduct;
import com.ge_products.api.GeProductAttribute;
import com.ge_products.api.GeProductBinaryObject;
import com.ge_products.api.GeProductDimension;
import com.ge_products.api.GeProductDocument;
import com.ge_products.api.GeProductFeature;
import com.ge_products.api.GeProductImage;
import com.ge_products.api.GeProductRelationship;
import com.ge_products.api.GeProductSearchCriteria;
import com.ge_products.api.GeProductSearchRangeFilter;
import com.ge_products.api.GeProductSearchRangeFilterType;
import com.ge_products.api.GeProductSearchResult;
import com.ge_products.api.GeProductSelectedDimension;
import com.newrelic.api.agent.NewRelic;

public final class GeProductApiHelper {
	
	private GeProductApiHelper() {}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeProductApiHelper.class);
	
	private static final String GE_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static final Integer FIRST_VALUE = 0;
	
	private static volatile ObjectMapper objectMapper;

	/* *************************************************************************************************
	 *  The following methods support converting results from JSON to Domain Objects
	 * *************************************************************************************************/
	
	/**
	 * De-serialize a JSON response into a GE Product Search Result structure.
	 * 
	 * @param rootNode - raw JSON response returned by GE Product API
	 * @return GeProductSearchResult - GE product data formatted for better use
	 */
	public static GeProductSearchResult getResultFromJsonNode(JsonNode rootNode) {
		if (rootNode == null) {
			return null;
		}

		if (objectMapper == null) {
			objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}

		// Get result node from root response node
		JsonNode resultNode = rootNode.get("results");

		GeProductSearchResult result = new GeProductSearchResult();

		// Branch name as key, containing object as value
		Map<String, JsonNode> branches = new HashMap<>();

		// Get all properties of the result objects
		for (JsonNode branchObject : resultNode) {
			Iterator<String> fieldNameIterator = branchObject.fieldNames();
			while (fieldNameIterator.hasNext()) {
				branches.put(fieldNameIterator.next(), branchObject);
			}
		}

		branches.forEach((name, value) -> processBranch(name, value, result));

		return result;
	}

	/**
	 * De-serialize a given branch below the top "results" node down through each
	 * JSON sub-branch below and update the structured result accordingly. 
	 * 
	 * @param branchName - name given for second tier JSON branch in GE Response
	 * @param branch - JSON node to pass down for further de-serialization
	 * @param result - Search result structure that is updated throughout
	 */
	private static void processBranch(String branchName, JsonNode branch, GeProductSearchResult result) {
		switch (branchName) {
			case "filters":
				// Available filters
				processFilterBranch(branch, result);
				break;
			case "selectedDimensions":
				// Search criteria given selected dimensions
				processSelectedDimensionsBranch(branch, result);
				break;
			case "searchCrumbs":
				// Search Crumbs - not yet implemented
				// Ignore - no clear usage for this data
				break;
			case "records":
				// Process product record list
				processRecordsBranch(branch, result);
				break;
			default:
				throw new RuntimeException("GE Product API Helper - Unknown response branch! " + branchName);
		}
	}

	/**
	 * De-serialize a JSON 'filter' branch from within the GE Product API response
	 * and update the structured result accordingly. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param branch - JSON node to de-serialization
	 * @param result - Search result structure that is updated
	 */
	private static void processFilterBranch(JsonNode branch, GeProductSearchResult result) {
		// Create the map of string to filter list
		Map<String, GeProductDimension> filters = new HashMap<>();

		// Get the filters map
		JsonNode filtersBranch = branch.get("filters");

		// Retrieve all field names of the filters map
		Iterator<String> i = filtersBranch.fieldNames();

		while (i.hasNext()) {
			// Get the filter group name
			String fieldName = i.next();

			// Get the filter group json
			String objectJson = filtersBranch.get(fieldName).toString();
			GeProductDimension filter;
			try {
				// Parse to GeProductDimension
				filter = objectMapper.readValue(objectJson, GeProductDimension.class);

				// Add to filter collection
				filters.put(fieldName, filter);
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'Filters'", e);
				NewRelic.noticeError(e);
			}
		}

		result.setFilters(filters);
	}
	
	/**
	 * De-serialize a JSON 'selectedDimensions' branch from within the GE Product API response
	 * and update the structured result accordingly. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param branch - JSON node to de-serialization
	 * @param result - Search result structure that is updated
	 */
	private static void processSelectedDimensionsBranch(JsonNode branch, GeProductSearchResult result) {
		// Get dimensions branch
		Iterator<JsonNode> dimensions = branch.get("selectedDimensions").elements();

		List<GeProductSelectedDimension> searchDimensions = new ArrayList<>();

		// Iterate all items in the dimension list
		while (dimensions.hasNext()) {
			JsonNode item = dimensions.next();

			// Create the dimension object
			GeProductSelectedDimension dimension = new GeProductSelectedDimension();
			String itemJson = item.toString();
			try {
				// Parse to GeProductSelectedDimension
				dimension = objectMapper.readValue(itemJson, GeProductSelectedDimension.class);

				// Add to selected Dimensions collection
				searchDimensions.add(dimension);
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'Selected Dimensions'", e);
				NewRelic.noticeError(e);
			}
		}

		result.setSelectedDimensions(searchDimensions);
	}

	/**
	 * De-serialize the JSON 'records' [products] branch from within the GE Product API response
	 * and update the structured result accordingly. 
	 * 
	 * Due to non-standard use of JSON, each product record sub-branch is processed by getProductRecord() 
	 * for further de-serialization.
	 * 
	 * Catches/logs any ObjectMapper exception encountered reported by getProductRecord()
	 * 
	 * @param branch - JSON node to de-serialization
	 * @param result - Search result structure that is updated
	 */
	private static void processRecordsBranch(JsonNode branch, GeProductSearchResult result) {
		JsonNode recordsBranch = branch.get("records");
		result.setTotalRecordCount(recordsBranch.get("totalNumRecs").asInt());
		result.setFirstRecordNumber(recordsBranch.get("firstRecNum").asInt());
		result.setLastRecordNumber(recordsBranch.get("lastRecNum").asInt());
		// Response sort options are currently ignored
		
		// Get the node holding the list of product records
		JsonNode productsNode = recordsBranch.get("products");

		// Create the record list
		List<GeProduct> records = new ArrayList<>();

		// Iterate product ids
		Iterator<String> productIdIterator = productsNode.fieldNames();
		while (productIdIterator.hasNext()) {
			// Get the record object from the array of record json objects
			try {
				String productId = productIdIterator.next();
				records.add(getProductRecord(productId, productsNode.get(productId)));
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'Product'", e);
				NewRelic.noticeError(e);
			}
		}

		result.setProducts(records);
	}

	/**
	 * De-serialize a JSON 'benefitCopy' branch from within the GE Product API response
	 * and add result to the input structured product domain object. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordBenefitCopy(JsonNode node, GeProduct record) {

		Iterator<JsonNode> childNodes = node.elements();

		List<GeProductFeature> featureList = new ArrayList<>();

		while (childNodes.hasNext()) {
			JsonNode featureNode = childNodes.next();

			GeProductFeature feature = new GeProductFeature();
			
			// Get the feature name
			String featureJson = featureNode.toString();
			try {
				// Parse to GeProductFeature
				feature = objectMapper.readValue(featureJson, GeProductFeature.class);

				// Add to feature collection
				featureList.add(feature);
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'Benefit Copy'", e);
				NewRelic.noticeError(e);
			}
		}

		record.setFeatures(featureList);
	}

	/**
	 * De-serialize each JSON 'attributes' branch from within the GE Product API response
	 * and add result to the input structured product domain object. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordAttributes(JsonNode node, GeProduct record) {
		List<GeProductAttribute> productAttributes = new ArrayList<>();

		Iterator<String> attributeNames = node.fieldNames();

		while (attributeNames.hasNext()) {
			// Build the attribute object
			GeProductAttribute attribute = new GeProductAttribute();

			// Get the attribute node
			String attributeName = attributeNames.next();
			JsonNode attributeNode = node.get(attributeName);
			String itemJson = attributeNode.toString();
			try {
				// Parse to GeProductAttribute
				attribute = objectMapper.readValue(itemJson, GeProductAttribute.class);
				attribute.setDescription(attributeName);
				// Add to attribute collection
				productAttributes.add(attribute);
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'Attributes'", e);
				NewRelic.noticeError(e);
			}
		}

		record.setAttributes(productAttributes);
	}

	/**
	 * De-serialize a JSON 'images' branch from within the GE Product API response
	 * and add result to the input structured product domain object.  
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordImages(JsonNode node, GeProduct record) {
		List<GeProductImage> images = new ArrayList<>();
		
		Iterator<String> fieldIterator = node.fieldNames();

		while (fieldIterator.hasNext()) {
			JsonNode imageNode = node.get(fieldIterator.next());

			GeProductImage image = new GeProductImage();
			image.setDataGroupID(imageNode.has("Data_Group_ID") ? imageNode.get("Data_Group_ID").textValue() : null);
			image.setDataID(imageNode.has("Data_ID") ? imageNode.get("Data_ID").textValue() : null);
			image.setName(imageNode.has("Name") ? imageNode.get("Name").textValue() : null);

			images.add(image);
		}

		record.setImages(images);
	}

	/**
	 * De-serialize a JSON 'documents' branch from within the GE Product API response
	 * and add result to the input structured product domain object. 
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordDocument(JsonNode node, GeProduct record) {
		List<GeProductDocument> documents = new ArrayList<>();
		
		Iterator<String> fieldIterator = node.fieldNames();

		while (fieldIterator.hasNext()) {
			JsonNode documentNode = node.get(fieldIterator.next());

			// Create product document sub object
			GeProductDocument document = new GeProductDocument();
			document.setDataID(documentNode.has("Data_ID") ? documentNode.get("Data_ID").textValue()  : null);
			document.setDateRemoved(documentNode.has("Date_Removed") ? DateUtils.stringToDate(documentNode.get("Date_Removed").textValue(), GE_DATE_FORMAT) : null);
			document.setDescription(documentNode.has("Description") ? documentNode.get("Description").textValue() : null);
			document.setDocumentType(documentNode.has("Document_Type") ? documentNode.get("Document_Type").textValue() : null);
			document.setDocumentType2(documentNode.has("Document_Type2") ? documentNode.get("Document_Type2").textValue() : null);
			document.setDocument(documentNode.has("Document") ? documentNode.get("Document").textValue() : null);
			document.setFileSize(documentNode.has("File_Size") ? documentNode.get("File_Size").textValue()  : null);
			document.setImageAndDocumentHierarchy(documentNode.has("Image_and_Document_Hierarchy") ? documentNode.get("Image_and_Document_Hierarchy").textValue() : null);
			document.setName(documentNode.has("Name") ? documentNode.get("Name").textValue() : null);
			document.setPubNumber(documentNode.has("Pub_Number") ? documentNode.get("Pub_Number").textValue() : null);

			// Add it to the list
			documents.add(document);
		}

		record.setDocuments(documents);
	}

	/**
	 * De-serialize a JSON 'binaryObject' branch from within the GE Product API response
	 * and add result to the input structured product domain object. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordBinaryObject(JsonNode node, GeProduct record) {

		Iterator<JsonNode> childNodes = node.elements();

		List<GeProductBinaryObject> binaryObjectList = new ArrayList<>();

		while (childNodes.hasNext()) {
			JsonNode binaryObjectNode = childNodes.next();

			GeProductBinaryObject binaryObject = new GeProductBinaryObject();
			
			// Get the feature name
			String binaryObjectJson = binaryObjectNode.toString();
			try {
				// Parse to GeProductBinaryObject
				binaryObject = objectMapper.readValue(binaryObjectJson, GeProductBinaryObject.class);

				// Add to feature collection
				binaryObjectList.add(binaryObject);
			} catch (IOException e) {
				LOGGER.error("GE Product API Helper exception processing Json Branch 'BinaryObjectDetails'", e);
				NewRelic.noticeError(e);
			}
		}

		// record.setBinaryObjects(binaryObjectList);		// TODO:  pending add of List<GeBinaryObject> to build-external GeProduct.java
	}
	/**
	 * De-serialize each JSON 'relationships' branch from within the GE Product API response
	 * and add result to the input structured product domain object. 
	 * 
	 * Uses ObjectMapper to de-serialize based on JSON class definition.
	 * 
	 * Catches/logs any ObjectMapper exception encountered
	 * 
	 * @param node - JSON node to de-serialization
	 * @param record - product data domain object that is updated
	 */
	private static void processProductRecordRelationships(JsonNode node, GeProduct record) {
		Map<String, List<GeProductRelationship>> relationships = new HashMap<>();

		Iterator<String> relationshipNames = node.fieldNames();

		while (relationshipNames.hasNext()) {
			// Relationship collection name
			String name = relationshipNames.next();

			// Relationship collection node
			JsonNode relationshipCollectionNode = node.get(name);

			// Iterator of child entries in current relationship collection
			Iterator<String> collectionItems = relationshipCollectionNode.fieldNames();

			List<GeProductRelationship> relationshipCollection = new ArrayList<>();
			
			while (collectionItems.hasNext()) {
				// Get the relationship entry
				JsonNode relationshipItemNode = relationshipCollectionNode.get(collectionItems.next());

				// Build the relationship domain object
				GeProductRelationship relationship = new GeProductRelationship();
				String itemJson = relationshipItemNode.toString();
				try {
					// Parse to GeProductRelationship
					relationship = objectMapper.readValue(itemJson, GeProductRelationship.class);

					// Add to relationship collection
					relationshipCollection.add(relationship);
				} catch (IOException e) {
					LOGGER.error("GE Product API Helper exception processing Json Branch 'Relationships'", e);
					NewRelic.noticeError(e);
				}
			}

			// Add the list to the lookup map
			relationships.put(name, relationshipCollection);
		}

		record.setRelationships(relationships);
	}

	/**
	 * De-serialize a JSON product branch from within the GE Product API response 
	 * and returns a new Product data domain object.
	 * 
	 * This JSON branch is not well structured, so other methods are called to 
	 * de-serialize each sub-branch.  ObjectMapper is called to de-serialize a list of 
	 * finishes (colors) and to handle the remaining name/value pairs as product 
	 * properties.. 
	 * 
	 * Throws any ObjectMapper exception encountered
	 * 
	 * @param productId - unique GE product identifier of JSON node to de-serialize
	 * @param productNode - JSON product node to de-serialization
	 * @param result - Search result structure that is updated
	 */
	private static GeProduct getProductRecord(String productId, JsonNode productNode) throws IOException {
		// We create the GE product record object
		GeProduct record = new GeProduct();
		record.setProductId(productId);
		
		Map<String, List<String>> properties = new HashMap<>();
		record.setProperties(properties);

		// Iterate the elements in the array (one product record is built from an array of objects)
		Iterator<JsonNode> productRecordFragmentIterator = productNode.elements();

		while (productRecordFragmentIterator.hasNext()) {
			// Get the fragment of the product record
			JsonNode fragment = productRecordFragmentIterator.next();

			// Iterate each field name in the object
			Iterator<String> fieldIterator = fragment.fieldNames();
			while (fieldIterator.hasNext()) {
				String field = fieldIterator.next();

				record.setManufacturer("GE");		// use GE for all products
				
				// If the field name matches here, it is a property on the domain object
				// If it does not match, it is a loose property, which is stored in a map
				switch (field) {
					// Search result object properties
					case "Description":
						record.setDescription(fragment.get(field).get(0).textValue());
						break;
					case "SKU":
						record.setProductSku(fragment.get(field).get(0).textValue());
						break;
					case "Color" :
						record.setColors(
								Arrays.asList(
									objectMapper.readValue(fragment.get(field).toString(), String[].class)
							)
						);
						break;
					case "BenefitCopy":
						processProductRecordBenefitCopy(fragment.get(field), record);
						break;
					case "Attributes":
						processProductRecordAttributes(fragment.get(field), record);
						break;
					case "Images":
						processProductRecordImages(fragment.get(field), record);
						break;
					case "Documents":
						processProductRecordDocument(fragment.get(field), record);
						break;
					case "Services":
						// Product Record Services - not yet implemented
						// Ignore - Have not found any example response with this content
						break;
					case "Relationships":
						processProductRecordRelationships(fragment.get(field), record);
						break;
					case "BinaryObjectDetails":
						if (fragment.get(field) != null && fragment.get(field).size() > 0) {
							String productIdentifier = (StringUtils.isEmpty(record.getProductId()) ? "Not Identified Yet" : record.getProductId());
							LOGGER.info("*****WARNING: BinaryObjectDetails found for GE product: " + productIdentifier);
						}
						// Binary Object Details related to feature are de-serialized in BenefitCopy
						// Have not found any example response with content at the Product level
						
						processProductRecordBinaryObject(fragment.get(field), record);
						break;
					default:
						// Schema-less product property, place into property map
						if (fragment.get(field) != null && fragment.get(field).size() > 0) {
							record.getProperties().put(
									field,
									Arrays.asList(
											objectMapper.readValue(fragment.get(field).toString(), String[].class)
									)
							);
						}	
						break;
				}	
			}
		}

		// Return the record, built as complete as it can after processing
		// the fragmented objects
		return record;
	}

	/**
	 * Converts list of elements of any type to list of strings representation
	 * 
	 * @param list - input elements
	 * @return string list of intput elements
	 */
	private static <T> List<String> getStringList(List<T> list) {
		List<String> stringList = new ArrayList<>();
		for (T i : list) {
			stringList.add(String.valueOf(i));
		}
		return stringList;
	}

	/**
	 * Combines strings with provided string delimiter
	 * 
	 * @param strings - input strings to include
	 * @param separator - delimiter 
	 * @return formatted string
	 */
	private static String combine(List<String> strings, String separator) {
		if (strings.isEmpty()) {
			return "";
		}

		StringBuilder combined = new StringBuilder();

		for (String i : strings) {
			combined.append(i).append(separator);
		}

		String res = combined.toString();
		return res.substring(0, res.length() - separator.length());
	}

	/**
	 * Format the search "range" content for use in a GE Product API query
	 * 
	 * @param filter - domain class containing the filter range content
	 * @return formatted query string fragment
	 */
	private static String getRangeFilterText(GeProductSearchRangeFilter filter) {
		String operatorValue = filter.getFilterType().getType();
		String query = filter.getKey() + "|" + operatorValue + "+" + filter.getValue();

		if (filter.getFilterType().equals(GeProductSearchRangeFilterType.BETWEEN)) {
			query += '+' + filter.getValue2();
			return query;
		}

		return query;
	}

	/**
	 * Format the search key/value content for use in a GE Product API query
	 * 
	 *  Note: this method does not currently support multiple values ('+' delimited) 
	 *  for a given key as specified in the API.
	 *   
	 * @param terms - key/value terms
	 * @return query string fragment
	 */
	private static String getSearchKeyValueText(Map<String, String> terms) {
		
		List<String> keys = new ArrayList<>();
		List<String> values = new ArrayList<>();

		for (Map.Entry<String, String> entry : terms.entrySet()) {
			keys.add(AwsVersion4RequestSigner.rfc3986EncodeString(entry.getKey()));
			values.add(AwsVersion4RequestSigner.rfc3986EncodeString(entry.getValue()));
		}

		String ntk = "Ntk=" + combine(keys, "|");
		String ntt = "&Ntt=" + combine(values, "|");

		return ntk + ntt;
	}

	/**
	 * Builds the query string portion of the HTTP URI per the GE Product API Rest
	 * form specification based on input search criteria.
	 * 
	 * See https://docs.oracle.com/cd/E29584_01/webhelp/mdex_basicDev/src/cbdv_urlparams_root.html
	 * for a list of Endeca url variables. We implement the ones given to us by GE.
	 * 
	 * @param criteria - domain class for identifying query search content
	 * @return String - query
	 */
	public static String getQueryStringFromSearchCriteria(GeProductSearchCriteria criteria) {
		if (criteria == null) {
			return "";
		}

		List<String> fragments = new ArrayList<>();

		// N - Navigation Descriptors
		String N = "N=";
		if (criteria.getNavDescriptors() == null) {
			N += "0";
		} else {
			N += combine(getStringList(criteria.getNavDescriptors()), "+");
		}

		// Add the nav descriptors variable to the query fragment list
		fragments.add(N);

		// Ne - Exposed product refinements
		if (criteria.getExposedRefinements() != null && !criteria.getExposedRefinements().isEmpty()) {
			fragments.add("Ne=" + combine(getStringList(criteria.getExposedRefinements()), "+"));
		}

		// Nf - Range filters
		if (criteria.getRangeFilters() != null && !criteria.getRangeFilters().isEmpty()) {
			List<String> rangeFilters = new ArrayList<>();
			for (GeProductSearchRangeFilter rf : criteria.getRangeFilters()) {
				rangeFilters.add(getRangeFilterText(rf));
			}
			fragments.add("Nf=" + combine(rangeFilters, "||"));
		}

		// No - Record start index
		if (criteria.getStartIndex() != null) {
			fragments.add("No=" + criteria.getStartIndex());
		}

		// Nrpp - Record number of product records to retrieve (supports paging through results)
		if (criteria.getNumberOfProducts() != null) {
			fragments.add("Nrpp=" + criteria.getNumberOfProducts());
		}

		// Ntk / Ntt - Search terms
		if (criteria.getSearchKeyValuePair() != null && !criteria.getSearchKeyValuePair().isEmpty()) {
			fragments.add(getSearchKeyValueText(criteria.getSearchKeyValuePair()));
		}

		// Ntx - Record search mode
		if (criteria.getRecordSearchMode() != null) {
			fragments.add("Ntx=" + criteria.getRecordSearchMode().value());
		}

		// Nty - Did you mean
		fragments.add("Nty=" + (criteria.isEnableDidYouMean() ? "1" : "0"));

		// D - Record Dimension search terms
		if (criteria.getDimensionSearchTerms() != null && !criteria.getDimensionSearchTerms().isEmpty()) {
			fragments.add("D=" + combine(criteria.getDimensionSearchTerms(), "+"));
		}

		// Dx - Record dimension search mode
		if (criteria.getDimensionSearchMode() != null) {
			fragments.add("Dx=" + criteria.getDimensionSearchMode().value());
		}

		return combine(fragments, "&");
	}


	
	
	
	public static boolean getGeProductStatus(GeProduct geProduct) {
		Boolean isActive = false;
		if (!getGeProperty(geProduct.getProperties(), "Product_Obsolete", FIRST_VALUE).isEmpty()) {
			isActive = !getGePropertyAsBoolean(geProduct.getProperties(), "Product_Obsolete", FIRST_VALUE, true);
		} else {
			isActive = !getGePropertyAsBoolean(geProduct.getProperties(), "Obsolete", FIRST_VALUE, true);	
		}
		return isActive;
	}
	

	

	

	/**
	 * Finds a specific property from a Mapped list of GE Property objects and returns 
	 * a single property value based on named property and desired index location within 
	 * the property values list. 
	 * 
	 * This method is typically used to extract out a property value to include
	 * in the constructed InboundProductProduct domain object or it's associated
	 * InboundProductBrand or InboundProductFinish domain objects.  In such cases, the
	 * property is typically found to have a single value, so index is often set to 0.
	 * 
	 * If the property is not found, an empty string is returned.
	 * 
	 * @param properties - Mapped list of GE Property objects
	 * @param propertyName - name of property for which value is requested
	 * @param index - index into the list of property values
	 * @return value - requested property value as a string (or empty string if not found)
	 */
	private static String getGeProperty(Map<String, List<String>> properties, String propertyName, Integer index) {
		String propertyValue = "";
		
		if (properties!= null && 
				properties.get(propertyName) != null &&
						properties.get(propertyName).size() > index) {
			propertyValue= properties.get(propertyName).get(index);
			
		}
		return propertyValue;
	}
	
	/**
	 * Finds a specific property from a Mapped list of GE Property objects and returns a single
	 * property value based on named property and desired index location within the property values list. 
	 * The return value is converted to Double.  If the property is not found, 0D or NULL is returned
	 * depending on the input missingAsNull parameter. 
	 * 
	 * @param properties - Mapped list of GE Property objects
	 * @param propertyName - name of property for which value is requested
	 * @param index - index into the list of property values
	 * @param missingAsNull - dictates return value if property is not found 
	 * 							(true returns NULL, false returns 0D)
	 * @return value - requested property value as Double (or NULL if missingAsNull=true)
	 */
	private static Double getGePropertyAsDouble(Map<String, List<String>> properties, String propertyName, Integer index, Boolean missingAsNull) {
		
		Double propertyValue = 0D;
		
		String propertyValueAsString = getGeProperty(properties, propertyName, index);
		if (!propertyValueAsString.isEmpty()) {
			propertyValue = new BigDecimal("" + propertyValueAsString).doubleValue();
		}
		return propertyValueAsString.isEmpty() && missingAsNull ? null : propertyValue;
	}
	
	/**
	 * Finds a specific property from a Mapped list of GE Property objects and returns a single
	 * property value based on named property and desired index location within the property values list. 
	 * The return value is converted to Long.  If the property is not found, a NULL is returned.
	 * 
	 * @param properties - Mapped list of GE Property objects
	 * @param propertyName - name of property for which value is requested
	 * @param index - index into the list of property values
	 * @return value - requested property value as Long or NULL if property is not found
	 */
	private static Long getGePropertyAsLong(Map<String, List<String>> properties, String propertyName, Integer index) {

		String propertyValueAsString = (getGeProperty(properties, propertyName, index));
		return StringUtils.isEmpty(propertyValueAsString) ? null : Long.parseLong(propertyValueAsString);
	}

	/**
	 * Finds a specific property from a Mapped list of GE Property objects and returns a single
	 * property value based on named property and desired index location within the property values list. 
	 * The return value is converted to Boolean.  If the property is not found, the input 
	 * missingAsTrue parameter identifies the value to return.
	 * 
	 * @param properties - Mapped list of GE Property objects
	 * @param propertyName - name of property for which value is requested
	 * @param index - index into the list of property values
	 * @param missingAsTrue - value returned if property is not found 
	 * @return value - requested property value as Boolean or value of missingAsTrue if property is not found
	 */
	private static Boolean getGePropertyAsBoolean(Map<String, List<String>> properties, String propertyName, Integer index, Boolean missingAsTrue) {
		
		String propertyValueAsString = getGeProperty(properties, propertyName, index);
		if (propertyValueAsString.isEmpty()) {
			return missingAsTrue;
		}
		return "TRUE".equalsIgnoreCase(propertyValueAsString);
	}
	
	/**
	 * Finds a specific property from a Mapped list of GE Property objects and returns a single
	 * property value based on named property and desired index location within the property values list. 
	 * The return value is converted to java.util.Date.  If the property is not found, a NULL is returned.
	 * 
	 * @param properties - Mapped list of GE Property objects
	 * @param propertyName - name of property for which value is requested
	 * @param index - index into the list of property values
	 * @return value - requested property value as Date or NULL if property is not found
	 */
	private static Date getGePropertyAsDate(Map<String, List<String>> properties, String propertyName, Integer index) {
		
		String propertyValueAsString = getGeProperty(properties, propertyName, index);
		return propertyValueAsString.isEmpty() ? null : DateUtils.stringToDate(propertyValueAsString, GE_DATE_FORMAT);
	}
	
	/**
	 * Logs information regarding GE content that was encountered but was not expected 
	 * (and is not currently supported).
	 * 
	 * @param fieldIdentifier - name of GE field encountered
	 * @param value - value of the GE Field encountered
	 * @param referenceIdentifier - a string to provide reference context (e.g., product identifier)
	 */
	private static void checkAndLogUnsupportedGeElement(String fieldIdentifier, String value, String referenceIdentifier) {
		if (!StringUtils.isEmpty(value)) {
			LOGGER.info("*****WARNING: GE field '" + fieldIdentifier + "' found (value: " + value + ") referenced by: " + referenceIdentifier);
		}
	}
	
	private static String correctFilename(String filename) {
		
		if (filename == null) {
			return null;
		}

		if (!filename.contains("_")) {		// avoid unnecessary checks
			return filename;
		}
		
		if (filename.contains("_jpg")) {
			filename = filename.replaceFirst("(?s)(.*)_jpg", "$1.jpg");
		} else if (filename.contains("_JPG")) {
			filename = filename.replaceFirst("(?s)(.*)_JPG", "$1.JPG");
		} else if (filename.contains("_gif")) {
			filename = filename.replaceFirst("(?s)(.*)_gif", "$1.gif");
	    } else if (filename.contains("_GIF")) {
			filename = filename.replaceFirst("(?s)(.*)_GIF", "$1.GIF");
		} else if (filename.contains("_png")) {
			filename = filename.replaceFirst("(?s)(.*)_png", "$1.png");
	    } else if (filename.contains("_PNG")) {
			filename = filename.replaceFirst("(?s)(.*)_PNG", "$1.PNG");
	    } 
		return filename;
	}
	
	public static JsonNode convertResultsToJson(String response) {
		
		JsonNode node = null;
		try {
			node = objectMapper.readTree(response);
		} catch (IOException e) {
		}
		return node;
	}
	
	
	private static void addToDeSerializedState(SortedMap<String, List<String>> deSerializedState, String key, String value) {
		List<String> mapValues= null;
		if (deSerializedState.containsKey(key)) {
			mapValues = deSerializedState.get(key);
		}	else {
			mapValues = new ArrayList<>();
			deSerializedState.put(key, mapValues);
		}
		mapValues.add(value);
	}
	
	private static void addToDeSerializedState(SortedMap<String, List<String>> deSerializedState, String key, List<String> values) {
		List<String> mapValues= null;
		if (deSerializedState.containsKey(key)) {
			mapValues = deSerializedState.get(key);
		}	else {
			mapValues = new ArrayList<>();
			deSerializedState.put(key, mapValues);
		}
		mapValues.addAll(values);	
	}
	
	public static BrandProduct convertToProductDistributionProduct(GeProduct geProduct, Integer sourceId) throws Exception {
		
		BrandProduct product = new BrandProduct();
		product.setSystemSourceId(sourceId);
		product.setProductId(geProduct.getProductId() == null ? geProduct.getProductSku() : geProduct.getProductId());
		product.setProductName(geProduct.getProductSku() != null?geProduct.getProductSku():"Unknown");					// N/A for GE
		//For GE put Title same as Short Description
		product.setShortDescription(getGeProperty(geProduct.getProperties(), "Product_Short_Description", FIRST_VALUE));		
		product.setDescription(geProduct.getDescription());
		product.setSku(geProduct.getProductSku());
		product.setManufacturer(geProduct.getManufacturer() == null ? "GE" : geProduct.getManufacturer());
		Date availableDate = getGePropertyAsDate(geProduct.getProperties(), "Product_First_Distribution_Date", FIRST_VALUE);
		if (availableDate == null) {
			availableDate = getGePropertyAsDate(geProduct.getProperties(), "Product_ADC_Availability_Date", FIRST_VALUE);
		}
		product.setDateAvailable(availableDate != null?availableDate:new Date());
		Date updatedDate = getGePropertyAsDate(geProduct.getProperties(), "Product_Updated_Date", FIRST_VALUE);
		if (updatedDate == null) {
			updatedDate =new Date();
		}
		product.setDateUpdated(updatedDate);
		
		List<JsonReference> jsonReferences = new ArrayList<>();
		product.setJsonReferences(jsonReferences);
		
		// Extract Finish information for product
		String color = null; 
		if (geProduct.getColors() != null && !geProduct.getColors().isEmpty()) {
			color = geProduct.getColors().get(0);
		} else {												// get from alternate property
			color = getGeProperty(geProduct.getProperties(), "Color.Name", FIRST_VALUE);	
		}
		product.setColor(StringUtils.isEmpty(color)?"Unknown":color);
		SortedMap<String, List<String>> deSerializedState = new TreeMap<>();
		if (!StringUtils.isEmpty(color)) {
			addToDeSerializedState(deSerializedState,"Color.Name", color);
			addToDeSerializedState(deSerializedState,"Product_Color.Color_Appearance", getGeProperty(geProduct.getProperties(), "Product_Color.Color_Appearance", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Color.Color_Appearance_Image", getGeProperty(geProduct.getProperties(), "Product_Color.Color_Appearance_Image", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Color.Image", getGeProperty(geProduct.getProperties(), "Color.Image", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Color.Data_ID", getGeProperty(geProduct.getProperties(), "Product_Color.Data_ID", FIRST_VALUE));
		} 
		JsonReference jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.FINISH);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		
		
		// Extract Brand information for product
		String brandName = getGeProperty(geProduct.getProperties(), "Brand", FIRST_VALUE);
		product.setBrandName(StringUtils.isEmpty(brandName)?"Unknown":brandName);
		deSerializedState = new TreeMap<>();
		if (!StringUtils.isEmpty(brandName)) {
			addToDeSerializedState(deSerializedState,"Brand",brandName);
			addToDeSerializedState(deSerializedState,"Brand.Display_Name",getGeProperty(geProduct.getProperties(), "Brand.DisplayName", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Brand.Subhead",getGeProperty(geProduct.getProperties(), "Product_Brand.Subhead", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Brand.Brand_Copy",getGeProperty(geProduct.getProperties(), "Product_Brand.Brand_Copy", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Brand.Data_ID",getGeProperty(geProduct.getProperties(), "Product_Brand.Data_ID", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Brand.ImageName",getGeProperty(geProduct.getProperties(), "Brand.ImageName", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Brand.Image",getGeProperty(geProduct.getProperties(), "Brand.Image", FIRST_VALUE));
		} 
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.BRAND);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		
		product.setRetailPrice(getGePropertyAsDouble(geProduct.getProperties(), "EstimatedRetailPrice", FIRST_VALUE, true));
		product.setUpc(getGeProperty(geProduct.getProperties(), "UPC", FIRST_VALUE));
		product.setIsActive(getGeProductStatus(geProduct));
		
		// other product fields are N/A to GE Products
		
		// Extract Product dimensions product
		String dimensions = getGeProperty(geProduct.getProperties(), "ProductDimensions", FIRST_VALUE);
		deSerializedState = new TreeMap<>();
		if (!StringUtils.isEmpty(dimensions)) {
			addToDeSerializedState(deSerializedState,"Product_Dimensions",dimensions);
			addToDeSerializedState(deSerializedState,"Product_Carton_Height", getGeProperty(geProduct.getProperties(), "Product_Carton_Height", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Carton_Length", getGeProperty(geProduct.getProperties(), "Product_Carton_Length", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Carton_Width", getGeProperty(geProduct.getProperties(), "Product_Carton_Width", FIRST_VALUE));
		}
			
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.DIMENSION);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		// Extract product attributes
		deSerializedState = new TreeMap<>();
		if (geProduct.getAttributes() != null && !geProduct.getAttributes().isEmpty()) {
			for (GeProductAttribute geAttribute : geProduct.getAttributes()) {
				addToDeSerializedState(deSerializedState,"Attribute_"+geAttribute.getDefinition()+"_"+geAttribute.getDisplayName(),geAttribute.getValue());
//				addToDeSerializedState(deSerializedState,"Attribute_"+geAttribute.getDefinition()+"_"+geAttribute.getDescription()+"_Value",geAttribute.getValue());
//				addToDeSerializedState(deSerializedState,"Attribute_"+geAttribute.getDefinition()+"_"+geAttribute.getDescription()+"_Priority",geAttribute.getPriority());
				addToDeSerializedState(deSerializedState,"Attribute_"+geAttribute.getDefinition()+"_"+geAttribute.getDisplayName()+"_UOM",geAttribute.getUOM());
				
			}
		}
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.ATTRIBUTE);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		// convert and save product custom properties 
		deSerializedState = new TreeMap<>();
		if (geProduct.getProperties() != null && !geProduct.getProperties().isEmpty()) {
			addToDeSerializedState(deSerializedState,"Searchable", getGeProperty(geProduct.getProperties(), "Searchable", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Created_Date", getGeProperty(geProduct.getProperties(), "Product_Created_Date", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Claims", getGeProperty(geProduct.getProperties(), "Claims", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"BaseModel", getGeProperty(geProduct.getProperties(), "BaseModel", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"hasMannuals", getGeProperty(geProduct.getProperties(), "hasMannuals", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_ApplProducts_Hierarchy", getGeProperty(geProduct.getProperties(), "Product_ApplProducts_Hierarchy", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Is_Part-Accessory", getGeProperty(geProduct.getProperties(), "Product_Is_Part-Accessory", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_UPC_Check_Digit", getGeProperty(geProduct.getProperties(), "Product_UPC_Check_Digit", FIRST_VALUE));
			addToDeSerializedState(deSerializedState,"Product_Package_Quantity", getGeProperty(geProduct.getProperties(), "Product_Package_Quantity", FIRST_VALUE));
			
		}	
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.CUSTOM_PROPERTY);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		// convert and save product categories from properties
		deSerializedState = new TreeMap<>();
		List<String> productCategories = geProduct.getProperties().get("Product_Category");
		List<String> productCommercialCategories = geProduct.getProperties().get("Commercial Categories");
		if (productCategories != null && !productCategories.isEmpty()) {
			product.setCategoryName(productCategories.get(0));
			addToDeSerializedState(deSerializedState,"Product_Category", productCategories);
		}
		if (productCommercialCategories != null && !productCommercialCategories.isEmpty()) {
			addToDeSerializedState(deSerializedState,"Commercial_Categories", productCommercialCategories);
		}
		if (StringUtils.isEmpty(product.getCategoryName())) {
			product.setCategoryName("Unknown");
		}
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.CATEGORY);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		// convert and save product images
		deSerializedState = new TreeMap<>();
		if (geProduct.getImages() != null && !geProduct.getImages().isEmpty()) {
			for (GeProductImage geImage : geProduct.getImages()) {
				addToDeSerializedState(deSerializedState,"Image_"+geImage.getDataGroupID(),geImage.getName());
				//addToDeSerializedState(deSerializedState,"Image_"+geImage.getDataGroupID()+"_Name",geImage.getName());
			}
		}

		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.IMAGE);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		// convert and save product accessory documents
		deSerializedState = new TreeMap<>();
		if (geProduct.getRelationships() != null && !geProduct.getRelationships().isEmpty()) {
			List<GeProductRelationship> relationships = geProduct.getRelationships().get("Accessories_");
			if (relationships != null && !relationships.isEmpty()) {
				 int i = 0;
				 for (GeProductRelationship relationship:relationships) {
					 addToDeSerializedState(deSerializedState,"Accessory_"+i+"_SKU", relationship.getSKU());
//					 addToDeSerializedState(deSerializedState,"Accessory_"+i+"_Brand_Subhead", relationship.getBrandSubhead());
//					 addToDeSerializedState(deSerializedState,"Accessory_"+i+"_Description", relationship.getDescription());
					 i++;
					 
				 }	 
			 }
			relationships = geProduct.getRelationships().get("Cross-Sell Colors_");
			if (relationships != null && !relationships.isEmpty()) {
				 int i = 0;
				 for (GeProductRelationship relationship:relationships) {
					 addToDeSerializedState(deSerializedState,"Cross-Sell Colors_"+i+"_SKU", relationship.getSKU());
//					 addToDeSerializedState(deSerializedState,"Cross-Sell Colors_"+i+"_Brand_Subhead", relationship.getBrandSubhead());
//					 addToDeSerializedState(deSerializedState,"Cross-Sell Colors_"+i+"_Description", relationship.getDescription());
					 i++;
					 
				 }	 
			 }
			
		}
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.ACCESSORY);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		 
		//Add product features
		deSerializedState = new TreeMap<>();
		if (geProduct.getProperties() != null && !geProduct.getProperties().isEmpty()) {
			Iterator<Entry<String, List<String>>> it = geProduct.getProperties().entrySet().iterator();
		    while (it.hasNext()) {
		    	Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>)it.next();
		    	if (entry.getKey().startsWith("ProductFeature")) {
		    		addToDeSerializedState(deSerializedState,entry.getKey(), entry.getValue());
		    	}
		    	
		    }
		}	
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.FEATURE);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		
		

		return product;
	}
}
