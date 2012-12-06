/*
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.guice.repository.testing.common;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Uses Guice to inject JUnit4 tests.
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner {
    private final Injector injector;


    /**
     * Creates a new GuiceTestRunner.
     *
     * @param classToRun the test class to run
     * @param modules    the Guice modules
     *
     * @throws InitializationError
     *          if the test class is malformed
     */
    public GuiceTestRunner(Class<?> classToRun, Module... modules) throws InitializationError {
        super(classToRun);
        injector = Guice.createInjector(modules);
    }

    @Override
    public Object createTest() {
        return injector.getInstance(getTestClass().getJavaClass());
    }

    @Override
    protected void validateZeroArgConstructor(List<Throwable> errors) {
        // Guice can inject constructors with parameters so we don't want this method to trigger an error
    }

    /**
     * Returns the Guice injector.
     *
     * @return the Guice injector
     */
    protected Injector getInjector() {
        return injector;
    }
}