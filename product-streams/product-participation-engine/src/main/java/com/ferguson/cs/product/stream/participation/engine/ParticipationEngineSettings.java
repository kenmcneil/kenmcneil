package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("participation-engine")
public class ParticipationEngineSettings {
	/** This user id is used to attribute changes made by the engine in event logs. */
	private Integer taskUserId;

	/** How long to wait between Participation event processor runs in ms. */
	private Integer scheduleFixedDelay;

	/** How long to wait before the first Participation event processor is run in ms. */
	private Integer scheduleInitialDelay;

	/**
	 * How long in minutes until a product has gone off sale before its base price can
	 * be updated from the latestBasePrice table.
	 */
	private Integer coolOffPeriod;

	/**
	 * If true, only Participation records with id >= testModeMinParticipationId will be processed.
	 * Makes debugging, manual testing, and load testing easier, by ignoring existing non-test records.
	 */
	private Boolean testModeEnabled;

	/** If test mode is enabled then ignore all Participation records with id less than this value. */
	private Integer testModeMinParticipationId;

	public Integer getTaskUserId() {
		return taskUserId;
	}

	public void setTaskUserId(Integer taskUserId) {
		this.taskUserId = taskUserId;
	}

	public Integer getScheduleFixedDelay() {
		return scheduleFixedDelay;
	}

	public void setScheduleFixedDelay(Integer scheduleFixedDelay) {
		this.scheduleFixedDelay = scheduleFixedDelay;
	}

	public Integer getScheduleInitialDelay() {
		return scheduleInitialDelay;
	}

	public void setScheduleInitialDelay(Integer scheduleInitialDelay) {
		this.scheduleInitialDelay = scheduleInitialDelay;
	}

	public Integer getCoolOffPeriod() {
		return coolOffPeriod;
	}

	public void setCoolOffPeriod(Integer coolOffPeriod) {
		this.coolOffPeriod = coolOffPeriod;
	}

	public Boolean getTestModeEnabled() {
		return testModeEnabled;
	}

	public void setTestModeEnabled(Boolean testModeEnabled) {
		this.testModeEnabled = testModeEnabled;
	}

	public Integer getTestModeMinParticipationId() {
		return testModeMinParticipationId;
	}

	public void setTestModeMinParticipationId(Integer testModeMinParticipationId) {
		this.testModeMinParticipationId = testModeMinParticipationId;
	}
}
