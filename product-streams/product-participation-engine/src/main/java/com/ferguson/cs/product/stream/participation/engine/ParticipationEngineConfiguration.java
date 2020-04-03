package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;

@Configuration
public class ParticipationEngineConfiguration {
	@Bean
	public ParticipationReader participationReader(ConstructService constructService) {
		return new ParticipationReader(constructService);
	}

	@Bean
	public ParticipationWriter participationWriter(ParticipationService participationService, ConstructService constructService) {
		return new ParticipationWriter(participationService, constructService);
	}

	@Bean
	public ParticipationProcessor participationProcessor(ParticipationReader participationReader, ParticipationWriter participationWriter) {
		return new ParticipationProcessor(participationReader, participationWriter);
	}

	@Bean
	public ParticipationEngineTask participationTask(ParticipationProcessor participationProcessor) {
		return new ParticipationEngineTask(participationProcessor);
	}
}
