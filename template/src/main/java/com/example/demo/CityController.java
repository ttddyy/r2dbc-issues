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

import java.util.concurrent.atomic.AtomicInteger;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.connectionfactory.init.DatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.DatabasePopulatorUtils;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mark Paluch
 */
@RestController
public class CityController {

	private final CityRepository repository;

	private final ConnectionFactory connectionFactory;

	public CityController(CityRepository repository, ConnectionFactory connectionFactory) {
		this.repository = repository;
		this.connectionFactory = connectionFactory;
	}

	@GetMapping("/")
	@Transactional
	public Flux<City> findCities() {
		return this.repository.findAll();
	}

	@GetMapping("/{id}")
	public Mono<City> findCityById(@PathVariable long id) {
		return this.repository.findById(id);
	}

	private AtomicInteger counter = new AtomicInteger();

	@GetMapping("/insert")
//	@Transactional
	public Mono<City> insert() {
		int count = counter.incrementAndGet();
		String name = "foo-" + count;
		String country = "country-" + count;
		City city = new City(name, country);
		System.out.println("AAA inserting name=" + name);
		Mono<City> result = this.repository.save(city);

		return result.doFinally(signal -> {
			System.out.println("AAA CONTROLLER signal=" + signal);
		});

//		Flux.empty()
//				.concatWith(result)
//				.doOnComplete(() -> {
//					System.out.println("AAA outer here");
//				})
//				.subscribe();

//		return result;
	}

	@GetMapping("/populator")
//	@Transactional
	public Mono<Void> populate() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setContinueOnError(false);
//		populator.setSeparator(this.properties.getSeparator());
//		if (this.properties.getSqlScriptEncoding() != null) {
//			populator.setSqlScriptEncoding(this.properties.getSqlScriptEncoding().name());
//		}
		ClassPathResource resource = new ClassPathResource("populate.sql");
		populator.addScript(resource);

		return DatabasePopulatorUtils.execute(populator, this.connectionFactory);
	}

}
