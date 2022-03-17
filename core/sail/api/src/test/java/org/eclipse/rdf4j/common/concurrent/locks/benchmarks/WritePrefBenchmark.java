/*******************************************************************************
 * Copyright (c) 2022 Eclipse RDF4J contributors.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Distribution License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/

package org.eclipse.rdf4j.common.concurrent.locks.benchmarks;

import org.eclipse.rdf4j.common.concurrent.locks.AbstractReadWriteLockManager;
import org.eclipse.rdf4j.common.concurrent.locks.WritePrefReadWriteLockManager;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class WritePrefBenchmark extends ReadWriteLockManagerBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include("benchmarks.WritePrefBenchmark.*") // adapt to run other benchmark tests
				.build();

		new Runner(opt).run();
	}

	@Override
	AbstractReadWriteLockManager getReadWriteLockManager() {
		return new WritePrefReadWriteLockManager("");
	}
}