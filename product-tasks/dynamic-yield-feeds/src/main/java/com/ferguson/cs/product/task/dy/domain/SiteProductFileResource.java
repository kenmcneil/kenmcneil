package com.ferguson.cs.product.task.dy.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;

public class SiteProductFileResource {
	Map<Integer, Resource> siteFileMap = new HashMap<>();

	public Map<Integer, Resource> getSiteFileMap() {
		return siteFileMap;
	}

	public void setSiteFileMap(Map<Integer, Resource> siteFileMap) {
		this.siteFileMap = siteFileMap;
	}
}
