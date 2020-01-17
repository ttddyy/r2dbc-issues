package com.example.demo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Tadaya Tsuyukubo
 */
@Service
public class CityService {
	private final CityRepository repository;

	public CityService(CityRepository repository) {
		this.repository = repository;
	}

	private AtomicLong counter = new AtomicLong();

	@Transactional
	public Mono<City> insert() {
		long count = counter.incrementAndGet();
		String name = "foo-" + count;
		String country = "country-" + count;
		City city = new City(name, country);
		Mono<City> result = this.repository.save(city).doOnNext(c -> {
//			System.out.println("BBB " + Thread.currentThread().getName());
		});

		return result;
	}

	@Transactional
	public Mono<Long> count() {
		return this.repository.count();
	}

}
