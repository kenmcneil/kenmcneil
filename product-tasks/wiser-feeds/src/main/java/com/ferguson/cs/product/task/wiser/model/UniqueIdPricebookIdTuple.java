package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Objects;

public final class UniqueIdPricebookIdTuple implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer uniqueId;
	private Integer pricebookId;

	public UniqueIdPricebookIdTuple(Integer uniqueId,Integer pricebookId) {
		this.uniqueId = uniqueId;
		this.pricebookId = pricebookId;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Integer getPricebookId() {
		return pricebookId;
	}

	public void setPricebookId(Integer pricebookId) {
		this.pricebookId = pricebookId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UniqueIdPricebookIdTuple other = (UniqueIdPricebookIdTuple)o;


		if(!Objects.equals(uniqueId, other.uniqueId)) {
			return false;
		}

		return Objects.equals(pricebookId,other.pricebookId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, pricebookId);
	}
}
