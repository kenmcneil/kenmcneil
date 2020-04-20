package com.ferguson.cs.product.stream.participation.engine.model;

public enum ParticipationItemSortColumn implements SortColumn {
	ID("id"),
	SALE_ID("saleId"),
	LAST_MODIFIED_DATE("lastModifiedDate"),
	SCHEDULE_START("schedule.from"),
	SCHEDULE_END("schedule.to"),
	STATUS("status");

	private final String columnName;
	private final String clientBinding;

	ParticipationItemSortColumn(String columnName) {
		this(columnName, columnName);
	}

	ParticipationItemSortColumn(String columnName, String clientBinding) {
		this.columnName = columnName;
		this.clientBinding = clientBinding;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String getClientBinding() {
		return clientBinding;
	}
}
