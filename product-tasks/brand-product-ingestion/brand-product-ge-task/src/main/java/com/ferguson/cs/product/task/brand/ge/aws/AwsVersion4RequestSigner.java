package com.ferguson.cs.product.task.brand.ge.aws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.http.HttpRequest;
import org.springframework.web.util.UriUtils;

public class AwsVersion4RequestSigner implements AwsRequestSigner {


	private static final byte[] DEFAULT_BODY = new byte[0];
	private static final Charset UTF8;
	private static final Set<String> IGNORED_HEADERS;

	private static final String LINE_BREAK = "\n";
	private static final String SLASH = "/";
	private static final String SPACE = " ";
	private static final String COMMA = ", ";
	private static final String ALGORITHM = "AWS4-HMAC-SHA256";

	private final String region;
	private final String apiKey;
	private final String secretKey;
	private final String service;

	private boolean enforceHost = false;

	static {
		try {
			UTF8 = Charset.forName("UTF-8");
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}

		IGNORED_HEADERS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		Collections.addAll(IGNORED_HEADERS, "Accept", "Content-Length");
	}

	public AwsVersion4RequestSigner(String region, String apiKey, String secretKey) {
		this(region, apiKey, secretKey, null);
	}

	public AwsVersion4RequestSigner(String region, String apiKey, String secretKey, String service) {
		this.region = region;
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.service = service;
	}

	@Override
	public boolean getEnforceHost() {
		return enforceHost;
	}

	@Override
	public void setEnforceHost(boolean enforceHost) {
		this.enforceHost = enforceHost;
	}

	/**
	 * Encode string per RFC3986 standard
	 * @param input
	 * @return - encoded string
	 */
	public static String rfc3986EncodeString(String input) {
		return UriUtils.encode(input, "UTF-8");
	}

	/**
	 * Update URI query to encode parameter values
	 * @param uri - URI containing query to encode
	 * @return - Updated query string
	 */
	private String getQuery(URI uri) {
		if (uri == null || uri.getQuery() == null) {
			return "";
		}

		SortedMap<String, String> sortedQuery = new TreeMap<>();
		String[] params = uri.getQuery().split("&");

		// Iterate query params
		for (int i = 0; i < params.length; ++i) {
			// Get the query fragment
			String[] key = params[i].split("=");

			// Add it to the query after encoding
			sortedQuery.put(rfc3986EncodeString(key[0]), key.length > 1 ? rfc3986EncodeString(key[1]) : "");
		}
		StringBuilder queryString = new StringBuilder();
		Iterator<String> queryIterator = sortedQuery.keySet().iterator();
		while (queryIterator.hasNext()) {
			// Iterate the ordered key set, get the key
			String key = queryIterator.next();

			// Append the query string
			queryString.append(key).append("=").append(sortedQuery.get(key)).append("&");
		}

		// Build the string
		String orderedQueryString = queryString.toString();

		// Substring it to remove the last & symbol
		return orderedQueryString.substring(0, orderedQueryString.lastIndexOf('&'));
	}

	@Override
	public void signRequest(HttpRequest request, byte[] body) throws IOException {
		if (body == null) {
			body = DEFAULT_BODY;
		}

		/*
		 * STEP 1: Define all of your request requirements - HTTP method, URL/URI, request body,
		 * etc.
		 */

		/*
		 * STEP 2: Create a date for headers and the credential string.
		 */
		Date today = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String amzDate = sdf.format(today);
		sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStamp = sdf.format(today);

		// add amz date to headers
		request.getHeaders().add("X-Amz-Date", amzDate);
		/*
		 * Step 3: based on request requirement construct a canonical URI based on the URI path.
		 */
		URI uri = request.getURI();
		String canonicalUri = uri.getPath();

		String effectiveService = this.service;
		if (effectiveService == null) {
			// We don't have an explicit service so we'll assume the sub-domain of the host is our service.
			effectiveService = (uri.getHost().indexOf('.') >= 0) ? uri.getHost().substring(0, uri.getHost().indexOf('.')) : uri.getHost();
		}

		if (enforceHost && !request.getHeaders().containsKey("Host")) {
			request.getHeaders().add("Host", uri.getHost());
		}

		/*
		 * Step 4: based on the request requirement construct the canonical query string. This is
		 * derived from the URI object provided.
		 */
		String canonicalQueryString = getQuery(uri);

		/*
		 * Step 5: Create the canonical headers and signed headers. Header names and value must be
		 * trimmed and lowercase, and sorted in ASCII order. Note that there is a trailing \n.
		 */
		String canonicalHeaders = processHeaders(request.getHeaders());

		/*
		 * Step 6: Create the list of signed headers. This lists the headers in the
		 * canonical_headers list, delimited with ";" and in alpha order. Note: The request can
		 * include any headers; canonical_headers and signed_headers lists those that you want to be
		 * included in the hash of the request. x-amz-date" is always required.
		 */
		String signedHeaders = processSignedHeaders(request.getHeaders());

		try {
			/*
			 * Step 7: If the request contains a body - you need to sha-256 hash the payload. For GET
			 * request it should be an empty string.
			 */
			String payloadHash = hash256(body);


			/*
			 * Step 8: Combine elements to create create canonical request.
			 */
			String canonicalRequest = buildCanonicalRequest(request.getMethod().name(), canonicalUri, canonicalQueryString, canonicalHeaders, signedHeaders, payloadHash);

			/*
			 * Step 9: Construct the credential scope and string to sign.
			 */
			String credentialScope = buildCredentialScope(dateStamp, region, effectiveService, "aws4_request");
			String stringToSign = buildStringToSign(amzDate, credentialScope, canonicalRequest);

			/*
			 * Step 10: Produce the signing key.
			 */
			byte[] signingKey = getSignatureKey(secretKey, dateStamp, region, effectiveService);

			/*
			 * Step 11: Generate the signature.
			 */
			String signature = bytesToHex(hmac(stringToSign, signingKey));

			/*
			 * Step 12: Construct authorization header and add it to passed headers.
			 */
			String authorizationHeader = buildAuthorizationHeader(apiKey, credentialScope, signedHeaders, signature);
			request.getHeaders().put("Authorization", Collections.singletonList(authorizationHeader));
		} catch(Exception ex) {
			throw new IOException("Unable to authorize request", ex);
		}
	}


	private static byte[] hmac(String data, byte[] key) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		return hmac(data.getBytes(UTF8), key);
	}

	private static byte[] hmac(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		final String algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data);
	}

	private static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] kSecret = ("AWS4" + key).getBytes(UTF8);
		byte[] kDate = hmac(dateStamp, kSecret);
		byte[] kRegion = hmac(regionName, kDate);
		byte[] kService = hmac(serviceName, kRegion);
		return hmac("aws4_request", kService);
	}

	private static String hash256(String data) throws NoSuchAlgorithmException {
		return hash256(data.getBytes(UTF8));
	}

	private static String hash256(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return bytesToHex(md.digest());
	}

	private static String bytesToHex(byte[] bytes) {
		return new String(Hex.encode(bytes));
	}

	private static String processHeaders(Map<String, List<String>> headers) {
		StringBuilder canonicalHeaders = new StringBuilder();
		// The headers have to be sorted alphabetically so we use a tree map
		Map<String, List<String>> canonical = new TreeMap<>();
		for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			if (IGNORED_HEADERS.contains(key)) {
				continue;
			}
			canonical.put(entry.getKey().toLowerCase(), entry.getValue());
		}

		// Now we can
		for (Map.Entry<String, List<String>> entry: canonical.entrySet()) {
			String key = entry.getKey();
			for(String value : entry.getValue()) {
				canonicalHeaders.append(key).append(":").append(trimAll(value)).append(LINE_BREAK);
			}
		}
		return canonicalHeaders.toString();
	}

	private static String processSignedHeaders(Map<String, ?> headers) {
		return headers.keySet()
				.stream()
				.filter(h -> !IGNORED_HEADERS.contains(h))
				.map(String::toLowerCase)
				.sorted()
				.collect(Collectors.joining(";"));
	}

	private static String buildCanonicalRequest(String method, String canonicalUri, String canonicalQueryString, String canonicalHeaders, String signedHeaders,
											   String payloadHash) {
		return method + LINE_BREAK +
				canonicalUri + LINE_BREAK +
				canonicalQueryString + LINE_BREAK +
				canonicalHeaders + LINE_BREAK +
				signedHeaders + LINE_BREAK +
				payloadHash;
	}

	private static String buildCredentialScope(String dateStamp, String region, String service, String requestType) {
		return dateStamp + SLASH + region + SLASH + service + SLASH + requestType;
	}

	private static String buildStringToSign(String amzDate, String credentialScope, String canonicalRequest) throws NoSuchAlgorithmException {
		return ALGORITHM + LINE_BREAK + amzDate + LINE_BREAK + credentialScope + LINE_BREAK + hash256(canonicalRequest);
	}

	private static String buildAuthorizationHeader(String apiKey, String credentialScope, String signedHeaders, String signature) {
		return ALGORITHM + SPACE + "Credential=" + apiKey + SLASH + credentialScope + COMMA +
		"SignedHeaders=" + signedHeaders + COMMA +
		"Signature=" + signature;
	}

	/**
	 * Trims all leading and trailing whitepsace and collapses multiple spaces into a single space.
	 * <p>see http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html</p>
	 * @param input the input to trim.
	 * @return {@code input} with all leading and trailing whitepsace and collapses multiple spaces into a single space.
	 */
	private static String trimAll(String input) {
		return input.trim().replaceAll("\\s{2,}", " ");
	}
}
