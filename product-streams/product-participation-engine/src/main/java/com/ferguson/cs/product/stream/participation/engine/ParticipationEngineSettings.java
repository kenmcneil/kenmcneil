package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("participation-engine")
@Component
public class ParticipationEngineSettings {
	Integer taskUserId;
	Integer scheduleFixedDelay;
	Integer scheduleInitialDelay;
	Boolean testModeEnabled;
	Boolean testModeLogParticipations;
	Integer testModeMinParticipationId;

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

	public Boolean getTestModeEnabled() {
		return testModeEnabled;
	}

	public void setTestModeEnabled(Boolean testModeEnabled) {
		this.testModeEnabled = testModeEnabled;
	}

	public Boolean getTestModeLogParticipations() {
		return testModeLogParticipations;
	}

	public void setTestModeLogParticipations(Boolean testModeLogParticipations) {
		this.testModeLogParticipations = testModeLogParticipations;
	}

	public Integer getTestModeMinParticipationId() {
		return testModeMinParticipationId;
	}

	public void setTestModeMinParticipationId(Integer testModeMinParticipationId) {
		this.testModeMinParticipationId = testModeMinParticipationId;
	}
}
