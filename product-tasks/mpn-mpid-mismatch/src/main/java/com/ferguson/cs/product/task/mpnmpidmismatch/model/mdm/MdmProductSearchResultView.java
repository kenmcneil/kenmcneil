package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmProductSearchResultView implements Serializable {

	private static final long serialVersionUID = 3L;

	private List<MdmProductSearchResult> mdmProductSearchResults;
	//private Duration took;
	private Integer total;
}
