package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.construct.ContentEventRepository;
import com.ferguson.cs.product.stream.participation.engine.construct.ParticipationItemRepository;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationItemizedV1Dao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationV1Dao;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationItemizedV1Lifecycle;
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
			ParticipationCoreDao participationCoreDao,
			ParticipationV1Dao participationV1Dao
	) {
		return new ParticipationV1Lifecycle(participationEngineSettings, participationCoreDao,
				participationV1Dao);
	}

	@Bean
	public ParticipationItemizedV1Lifecycle participationItemizedV1Lifecycle(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationItemizedV1Dao participationItemizedV1Dao
	) {
		return new ParticipationItemizedV1Lifecycle(participationEngineSettings,
				participationCoreDao, participationItemizedV1Dao);
	}

	@Bean
	public ParticipationLifecycleService participationLifecycleService(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationV1Lifecycle participationV1Lifecycle,
			ParticipationItemizedV1Lifecycle participationItemizedV1Lifecycle
	) {
		return new ParticipationLifecycleServiceImpl(participationEngineSettings,
				participationCoreDao, participationV1Lifecycle, participationItemizedV1Lifecycle);
	}
}
