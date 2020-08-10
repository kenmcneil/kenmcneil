package com.ferguson.cs.product;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.ferguson.cs.data.ApiDataAccessAutoConfiguration;

import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@EnableSpringDataWebSupport
@Import(ApiDataAccessAutoConfiguration.class)
public class ProductApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).run(args);
	}

	//This is needed because the reactive lettuce driver relies on the reactor scheduler
	//to make a blocking call when getting a redis connection. This will result in Reactor's scheduler thread pool
	//being created. If we don't shut the thread pool down, it results in warnings on shutdown.
	@Bean(destroyMethod = "destroy")
	public DisposableBean reactorShutdown() {
		return Schedulers::shutdownNow;
	}
}
