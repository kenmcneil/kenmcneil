package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.construct.ContentEventRepository;
import com.ferguson.cs.product.stream.participation.engine.construct.ParticipationItemRepository;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationV1Lifecycle;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ParticipationEngineSettings.class)
public class ParticipationEngineConfiguration {
	@Bean
	public ConstructService constructService(
			ParticipationEngineSettings participationEngineSettings,
			ContentEventRepository contentEventRepository,
			ParticipationItemRepository participationItemRepository
	) {
		return new ConstructServiceImpl(participationEngineSettings, contentEventRepository,
				participationItemRepository);
	}

	@Bean
	public ParticipationWriter participationWriter(
			ParticipationLifecycleService participationLifecycleService,
			ConstructService constructService
	) {
		return new ParticipationWriter(participationLifecycleService, constructService);
	}

	@Bean
	public ParticipationProcessor participationProcessor(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationLifecycleService participationLifecycleService,
			ParticipationWriter participationWriter
	) {
		return new ParticipationProcessor(participationEngineSettings, constructService,
				participationLifecycleService, participationWriter);
	}

	@Bean
	public ParticipationEngineTask participationTask(
			ParticipationProcessor participationProcessor,
			ParticipationEngineSettings participationEngineSettings
	) {
		return new ParticipationEngineTask(participationProcessor, participationEngineSettings);
	}

	@Bean
	public ParticipationV1Lifecycle participationV1Lifecycle(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationDao participationDao
	) {
		return new ParticipationV1Lifecycle(participationEngineSettings, participationDao);
	}

	@Bean
	public ParticipationLifecycleService participationLifecycleService(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationDao participationDao,
			ParticipationV1Lifecycle participationV1Lifecycle
	) {
		return new ParticipationLifecycleServiceImpl(participationEngineSettings, participationDao,
				participationV1Lifecycle);
	}
}
