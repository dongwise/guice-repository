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

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;

/**
 * Builder of {@link RepositoryBinding}. Builder instance constructed when you invoke {@link
 * RepositoryBinder#bind(Class)}.
 * <p>
 * Each binding process should be finished with {@link RepositoryBindingBuilder#to(String)} or {@link
 * RepositoryBindingBuilder#withSelfDefinition()}
 * </p>
 *
 * @author Alexey Krylov
 * @see RepositoryBinder
 * @since 07.12.12
 */
public interface RepositoryBindingBuilder {

    /*===========================================[ INTERFACE METHODS ]==============*/

    RepositoryBindingBuilder withCustomImplementation(Class customRepositoryClass);

    RepositoryBindingBuilder withNamedQueries(NamedQueries namedQueries);

    RepositoryBindingBuilder withQueryLookupStrategyKey(QueryLookupStrategy.Key key);

    void to(String persistenceUnitName);

    /**
     * Use this method to finish building process when you don't want to specify {@code persistenceUnitName}. In this
     * case persistence unit name will be resolved by {@link JpaRepositoryModule#extractAnnotationsPersistenceUnitName(Class)}.
     * If repository interface has no {@link PersistenceContext} or {@link Transactional} annotations then default
     * persistence unit will be used.
     */
    void withSelfDefinition();
}