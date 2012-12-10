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

package com.google.code.guice.repository.testing.junit.multi;

import com.google.code.guice.repository.configuration.JpaRepositoryModule;
import com.google.code.guice.repository.configuration.RepositoryBinder;
import com.google.code.guice.repository.testing.common.GuiceTestRunner;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import org.junit.runners.model.InitializationError;

/**
 * MultiUnitsTestRunner -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
public class MultiPersistenceUnitsTestRunner extends GuiceTestRunner {

    /*===========================================[ CONSTRUCTORS ]===============*/

    public MultiPersistenceUnitsTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new JpaRepositoryModule("test-h2", "test-h2-secondary") {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(UserRepository.class).withSelfDefinition();
                binder.bind(AccountRepository.class).withSelfDefinition();
                binder.bind(UserDataRepository.class).to("test-h2-secondary");
            }
        });
    }
}
