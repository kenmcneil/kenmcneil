package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CostUpdateJob implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String processType;
	private String jobName;
	private Integer userId;
	private Date createdOn;
	private Date processOn;
	private String processNow;
	private Boolean cancel;
	private String status;

}
