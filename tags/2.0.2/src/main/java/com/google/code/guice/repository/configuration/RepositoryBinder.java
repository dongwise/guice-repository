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

package com.google.code.guice.repository.configuration;

import com.google.code.guice.repository.spi.JpaRepositoryProvider;
import com.google.inject.Injector;

import java.util.Collection;

/**
 * Collects binding configuration information which will be used in {@link Injector} creation
 * process.
 * Example:
 * <pre>
 * bind(MyRepository.class).to("persistence-unit1");
 * bind(MyCustomerRepository.class).withCustomImplementation(MyCustomerRepositoryImpl.class).withSelfDefinition();
 * </pre>
 *
 * @author Alexey Krylov
 * @see JpaRepositoryModule#configure()
 * @see JpaRepositoryProvider
 * @since 07.12.12
 */
public interface RepositoryBinder {

    /*===========================================[ INTERFACE METHODS ]==============*/

    RepositoryBindingBuilder bind(Class repositoryClass);

    Collection<RepositoryBinding> getBindings();
}
