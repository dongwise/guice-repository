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

package com.google.code.guice.repository.spi;

import com.google.code.guice.repository.configuration.RepositoryBinding;
import com.google.code.guice.repository.configuration.RepositoryBindingBuilder;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * Default implementation of {@link RepositoryBindingBuilder}.
 *
 * @author Alexey Krylov
 * @since 07.12.12
 */
public class DefaultRepositoryBindingBuilder implements RepositoryBindingBuilder {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private Class repositoryClass;
    private Class customRepositoryClass;
    private NamedQueries namedQueries;
    private QueryLookupStrategy.Key key;
    private String persistenceUnitName;

    /*===========================================[ CONSTRUCTORS ]=================*/

    protected DefaultRepositoryBindingBuilder(Class repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public RepositoryBindingBuilder withCustomImplementation(Class customRepositoryClass) {
        this.customRepositoryClass = customRepositoryClass;
        return this;
    }

    @Override
    public RepositoryBindingBuilder withNamedQueries(NamedQueries namedQueries) {
        this.namedQueries = namedQueries;
        return this;
    }

    @Override
    public RepositoryBindingBuilder withQueryLookupStrategyKey(QueryLookupStrategy.Key key) {
        this.key = key;
        return this;
    }

    @Override
    public void to(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        build();
    }

    @Override
    public void withSelfDefinition() {
        build();
    }

    protected RepositoryBinding build() {
        AccessibleRepositoryBinding binding = new AccessibleRepositoryBinding(repositoryClass);
        binding.setCustomRepositoryClass(customRepositoryClass);
        binding.setNamedQueries(namedQueries);
        binding.setQueryLookupStrategyKey(key);
        binding.setPersistenceUnitName(persistenceUnitName);
        return binding;
    }
}
