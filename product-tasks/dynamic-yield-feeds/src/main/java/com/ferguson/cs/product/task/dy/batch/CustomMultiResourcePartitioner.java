package com.ferguson.cs.product.task.dy.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.ferguson.cs.product.task.dy.domain.SiteProductFileResource;

public class CustomMultiResourcePartitioner implements Partitioner {
	private static final String PARTITION_KEY = "sitePartition";
	private SiteProductFileResource resources;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> partition = new HashMap<>(gridSize);

		for (Map.Entry<Integer, Resource> entry : resources.getSiteFileMap().entrySet()) {
			ExecutionContext context = new ExecutionContext();
			Integer siteId = entry.getKey();
			Assert.state(resources.getSiteFileMap().get(siteId).exists(), "Resource does not exist: "
				+ resources.getSiteFileMap().get(siteId).getFilename());
			context.putString("fileName", resources.getSiteFileMap().get(siteId).getFilename());
			context.putString("siteId", siteId.toString());
			partition.put(PARTITION_KEY + siteId, context);
		}
		return partition;
	}

	public void setResources(SiteProductFileResource resources) {
		this.resources = resources;
	}
}
