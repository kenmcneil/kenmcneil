package com.ferguson.cs.product.task.feitrilogympidsync.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeiTrilogyMpidSync implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer uniqueId;
	private Integer mpid;
	private Boolean inTrilogy;
}
