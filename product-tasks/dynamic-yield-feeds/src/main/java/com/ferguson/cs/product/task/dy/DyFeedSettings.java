package com.ferguson.cs.product.task.dy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties("dy")
@Component
@Data
public class DyFeedSettings {
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpRoot;
	private String ftpPrivateKey;
	private String tempFilePrefix = "productfeed";
	private String tempFileSuffix = ".csv";
	private Map<Integer, String> siteUsername = new HashMap<>();
	private List<String> excludedBrands;
	private List<Integer> restrictionPolicies = new ArrayList<>();
	private List<Integer> stores = new ArrayList<>();
	private Integer minimumRecordCount;
}
