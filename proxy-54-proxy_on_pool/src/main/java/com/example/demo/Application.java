/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.listener.ProxyExecutionListener;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableTransactionManagement
public class Application {

	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Configuration(proxyBeanMethods = false)
	static class R2dbcConfiguration {

//		@Bean
		public ConnectionFactory connectionFactoryOnly(R2dbcProperties properties,
				List<ConnectionFactoryOptionsBuilderCustomizer> customizers) {
			ConnectionFactory connectionFactory = ConnectionFactoryBuilder.create(properties).customize(customizers)
					.build();
			return connectionFactory;
		}

		@Bean(destroyMethod = "dispose")
		public ConnectionFactory connectionFactoryWithPool(R2dbcProperties properties,
				List<ConnectionFactoryOptionsBuilderCustomizer> customizers) {
			ConnectionFactory connectionFactory = ConnectionFactoryBuilder.create(properties).customize(customizers)
					.build();
			R2dbcProperties.Pool pool = properties.getPool();
			ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory)
					.maxSize(pool.getMaxSize()).initialSize(pool.getInitialSize()).maxIdleTime(pool.getMaxIdleTime());
			if (StringUtils.hasText(pool.getValidationQuery())) {
				builder.validationQuery(pool.getValidationQuery());
			}
			return new ConnectionPool(builder.build());
		}


		@Primary
		@Bean
		public ConnectionFactory wrapWithProxy(ConnectionFactory original) {
			return ProxyConnectionFactory.builder(original)
					.listener(new MyQueryListener())
					.build();
		}

		//		@Bean
		public ApplicationRunner runner(CityService cityService) {
			return args -> {
				int repeat = 100;
				repeat = 1_000;
//				repeat = 100_000;

				Scheduler scheduler = Schedulers.newParallel("my-parallel", 10);
				cityService.insert()
						.subscribeOn(scheduler)
						.repeat(repeat)
//						.then(cityService.count())
//						.doOnNext(count -> {
//							logger.info("city count={}", count);
//						})
						.subscribe();
			};
		}

		@Bean
		public ApplicationRunner runner2(CityService cityService) {
			return args -> {
				Flux.interval(Duration.ofMillis(20))
						.flatMap(t -> cityService.insert())
						.doOnNext(city -> {
							logger.info("id={}", city.getId());
//							System.out.println("aaa");
						})
						.doOnError(exception -> {
							logger.error("** ERROR **", exception);
						})
						.subscribe();
			};
		}
	}

	static class MyQueryListener implements ProxyExecutionListener {

		private AtomicLong queryCount = new AtomicLong();

		private QueryExecutionInfoFormatter formatter = new QueryExecutionInfoFormatter().showQuery();

		@Override
		public void afterQuery(QueryExecutionInfo execInfo) {
			logger.info("[{}] [{}] {} ", queryCount.incrementAndGet(), Thread.currentThread().getName(), formatter.format(execInfo));
		}
	}
}
