package com.ferguson.cs.product.stream.participation.engine;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("participation-engine")
public class ParticipationEngineSettings {

	/** How long to wait between Participation event processor runs in ms. */
	private Integer scheduleFixedDelay;

	/** How long to wait before the first Participation event processor is run in ms. */
	private Integer scheduleInitialDelay;

	/**
	 * How long until a product has gone off sale before its base price can
	 * be updated from the latestBasePrice table.
	 */
	private Duration coolOffPeriod;

	/**
	 * If true, only Participation records with id >= testModeMinParticipationId will be processed.
	 * Makes debugging, manual testing, and load testing easier, by ignoring existing non-test records.
	 */
	private Boolean testModeEnabled;

	/** If test mode is enabled then ignore all Participation records with id less than this value. */
	private Integer testModeMinParticipationId;
}
