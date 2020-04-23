package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ParticipationEngineSettings.class)
public class ParticipationEngineConfiguration {
	@Bean
	public ParticipationWriter participationWriter(
			ParticipationService participationService,
			ConstructService constructService
	) {
		return new ParticipationWriter(participationService, constructService);
	}

	@Bean
	public ParticipationProcessor participationProcessor(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationService participationService,
			ParticipationWriter participationWriter
	) {
		return new ParticipationProcessor(
				participationEngineSettings, constructService, participationService, participationWriter);
	}

	@Bean
	public ParticipationEngineTask participationTask(
			ParticipationProcessor participationProcessor,
			ParticipationEngineSettings participationEngineSettings
	) {
		return new ParticipationEngineTask(participationProcessor, participationEngineSettings);
	}
}
