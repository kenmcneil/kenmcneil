package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSyncJob implements Serializable {

	private static final long serialVersionUID = 2L;

	private Integer id;

	private String userId;
	private String clerkId;
	private String reviewerId;

	private Integer userOmcId;
	private Integer clerkOmcId;
	private Integer reviewerOmcId;

	private String jiraTracker;
	private Integer jobTypeId;
	private String jobName;
	private String jobDescription;
	private Integer manufacturerId;
	private String manufacturerName;
	private String fileName;
	private Boolean isDelete;
	private ProductSyncStatus status;
	private Date dateCreated;
	private Integer familyUpdateCount;
	private Integer finishUpdateCount;
	private Integer pidWarningCount;
	private Integer finishWarningCount;
	private Integer skuWarningCount;
	private Integer affectedProductUidCount;
	private Integer affectedFamilyIdCount;
}
