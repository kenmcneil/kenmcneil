package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;

@Configuration
@EnableConfigurationProperties(ParticipationEngineSettings.class)
public class ParticipationEngineConfiguration {
	@Bean
	public ParticipationWriter participationWriter(ParticipationService participationService, ConstructService constructService) {
		return new ParticipationWriter(participationService, constructService);
	}

	@Bean
	public ParticipationProcessor participationProcessor(ConstructService constructService, ParticipationWriter participationWriter) {
		return new ParticipationProcessor(constructService, participationWriter);
	}

	@Bean
	public ParticipationEngineTask participationTask(
			ParticipationProcessor participationProcessor,
			ParticipationEngineSettings participationEngineSettings
	) {
		return new ParticipationEngineTask(participationProcessor, participationEngineSettings);
	}
}
