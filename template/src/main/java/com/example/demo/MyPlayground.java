/*
 * Copyright 2012-2020 the original author or authors.
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

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Tadaya Tsuyukubo
 */
public class MyPlayground {

	public static void main(String[] args) {
		bb();
	}

	private static void bb() {

		Mono<String> mono = Mono.defer(() -> Mono.just("foo")).doOnNext(s -> {
			System.out.println("mono doOnNext ");
		}).doAfterTerminate(() -> {
			System.out.println("mono doAfterTerminate");
		}).doFinally(signalType -> {
			System.out.println("mono doFinally");
		}).doOnSuccess(s -> {
			System.out.println("mono doOnSuccess");
		});

		mono.subscribe();

		// Flux<String> flux = Flux.defer(() -> mono)
		// .doOnSubscribe(subscription -> {
		// System.out.println("Flux doOnSubscribe");
		// })
		// .doAfterTerminate(() -> {
		// System.out.println("Flux doAfterTerminate");
		// })
		// .doOnNext(s -> {
		// System.out.println("Flux doOnNext");
		// })
		// .doOnComplete(() -> {
		// System.out.println("Flux doOnComplete");
		// })
		// .doFinally(signalType -> {
		// System.out.println("Flux doFinally");
		// });

		Flux<String> flux = Flux.just("foo", "bar").doOnNext(s -> {
			System.out.println("Flux doOnNext");
		}).doAfterTerminate(() -> {
			System.out.println("Flux doAfterTerminate");
		}).doOnComplete(() -> {
			System.out.println("Flux doOnComplete");
		}).doFinally(signalType -> {
			System.out.println("Flux doFinally");
//		}).doAfterTerminate(() -> {
//			System.out.println("Flux doAfterTerminate");
		});

		flux.subscribe();

	}

	private static void aa() {

		Flux<String> f = Flux.just("foo", "bar").doOnNext(s -> {
			System.out.println("AAA[source]" + s);
		}).doFinally(signalType -> {
			System.out.println("AAA[source] doFinally");
		});
		Mono<String> m = Mono.just("foo").doOnNext(s -> {
			System.out.println("AAA[source]" + s);
		}).doFinally(signalType -> {
			System.out.println("AAA[source] doFinally");
		}).switchIfEmpty(Mono.just("empty"));

		// f.subscribe();

		Flux<String> flux = Flux.empty().ofType(String.class).doOnSubscribe(s -> {
			System.out.println("AAA doOnSubscribe");
		}).concatWith(m).doOnComplete(() -> {
			System.out.println("AAA doOnComplete");
		});

		flux.subscribe();
	}

}
