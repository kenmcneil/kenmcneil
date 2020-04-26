package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationItemPartial {
	private Integer id;
	private Integer participationId;
	private Integer saleId;
	private Date startDate;
	private Date endDate;
	private Integer lastModifiedUserId;
	private Boolean isActive;
}
