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
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationItemizedV1V2Dao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationV1Dao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationV2Dao;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationCouponV1Lifecycle;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationItemizedV1Lifecycle;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationItemizedV2Lifecycle;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationV1Lifecycle;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationV2Lifecycle;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ParticipationEngineSettings.class)
public class ParticipationEngineConfiguration {

	@Bean
	public ConstructService constructService(
			ContentEventRepository contentEventRepository,
			ParticipationItemRepository participationItemRepository
	) {
		return new ConstructServiceImpl(contentEventRepository, participationItemRepository);
	}

	@Bean
	public ParticipationWriter participationWriter(
			ConstructService constructService,
			ParticipationLifecycleService participationLifecycleService
	) {
		return new ParticipationWriter(constructService, participationLifecycleService);
	}

	@Bean
	public ParticipationProcessor participationProcessor(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationLifecycleService participationLifecycleService,
			ParticipationWriter participationWriter
	) {
		return new ParticipationProcessor(
				participationEngineSettings,
				constructService,
				participationLifecycleService,
				participationWriter);
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
	public ParticipationV2Lifecycle participationV2Lifecycle(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationV2Dao participationV2Dao
	) {
		return new ParticipationV2Lifecycle(participationEngineSettings, participationCoreDao,
				participationV2Dao);
	}

	@Bean
	public ParticipationItemizedV1Lifecycle participationItemizedV1Lifecycle(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationItemizedV1V2Dao participationItemizedV1V2Dao
	) {
		return new ParticipationItemizedV1Lifecycle(participationEngineSettings,
				participationCoreDao, participationItemizedV1V2Dao);
	}

	@Bean
	public ParticipationItemizedV2Lifecycle participationItemizedV2Lifecycle(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationItemizedV1V2Dao participationItemizedV1V2Dao
	) {
		return new ParticipationItemizedV2Lifecycle(participationEngineSettings,
				participationCoreDao, participationItemizedV1V2Dao);
	}

	@Bean
	public ParticipationCouponV1Lifecycle participationCouponV1Lifecycle(
			ParticipationCoreDao participationCoreDao
	) {
		return new ParticipationCouponV1Lifecycle(participationCoreDao);
	}

	@Bean
	public ParticipationLifecycleService participationLifecycleService(
			ParticipationEngineSettings participationEngineSettings,
			ParticipationCoreDao participationCoreDao,
			ParticipationV1Lifecycle participationV1Lifecycle,
			ParticipationV2Lifecycle participationV2Lifecycle,
			ParticipationItemizedV1Lifecycle participationItemizedV1Lifecycle,
			ParticipationItemizedV2Lifecycle participationItemizedV2Lifecycle,
			ParticipationCouponV1Lifecycle participationCouponV1Lifecycle
	) {
		return new ParticipationLifecycleServiceImpl(participationEngineSettings,
				participationCoreDao,
				participationV1Lifecycle,
				participationV2Lifecycle,
				participationItemizedV1Lifecycle,
				participationItemizedV2Lifecycle,
				participationCouponV1Lifecycle
		);
	}
}
