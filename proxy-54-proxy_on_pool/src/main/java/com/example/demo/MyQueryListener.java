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

import java.util.concurrent.atomic.AtomicInteger;

import io.r2dbc.proxy.core.MethodExecutionInfo;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.listener.ProxyExecutionListener;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;

/**
 *
 * @author Tadaya Tsuyukubo
 */
public class MyQueryListener implements ProxyExecutionListener {

	AtomicInteger queryCount = new AtomicInteger();

	AtomicInteger methodCount = new AtomicInteger();

	AtomicInteger sequence = new AtomicInteger();

	@Override
	public void beforeQuery(QueryExecutionInfo execInfo) {
		QueryExecutionInfoFormatter formatter = new QueryExecutionInfoFormatter().showQuery();
		String s;
//		String s = String.format("AAA(%d) query=%s, success=%s", queryCount.getAndIncrement(), formatter.format(execInfo), execInfo.isSuccess());
//		System.out.println(s);

		s = String.format("[%3d] [before-query] %s", sequence.incrementAndGet(), formatter.format(execInfo));
		System.out.println(s);
	}
	@Override
	public void afterQuery(QueryExecutionInfo execInfo) {
		QueryExecutionInfoFormatter formatter = new QueryExecutionInfoFormatter().showQuery();
		String s;
//		String s = String.format("AAA(%d) query=%s, success=%s", queryCount.getAndIncrement(), formatter.format(execInfo), execInfo.isSuccess());
//		System.out.println(s);

		s = String.format("[%3d] [after-query] %s", sequence.incrementAndGet(), formatter.format(execInfo));
		System.out.println(s);
	}

	@Override
	public void beforeMethod(MethodExecutionInfo methodInfo) {
		String s;
//		s = String.format("AAA(%d) beforeMethod: method=%s", methodCount.get(), methodInfo.getMethod());
//		System.out.println(s);

		s = String.format("[%3d] [before-method] %s#%s", sequence.incrementAndGet(), methodInfo.getMethod().getDeclaringClass().getName(), methodInfo.getMethod().getName());
		System.out.println(s);
	}

	@Override
	public void afterMethod(MethodExecutionInfo methodInfo) {
		String s;
//		s = String.format("AAA(%d) afterMethod: method=%s", methodCount.getAndIncrement(), methodInfo.getMethod());
//		System.out.println(s);

		s = String.format("[%3d] [after-method] %s#%s", sequence.incrementAndGet(), methodInfo.getMethod().getDeclaringClass().getName(), methodInfo.getMethod().getName());
		System.out.println(s);
	}
}
