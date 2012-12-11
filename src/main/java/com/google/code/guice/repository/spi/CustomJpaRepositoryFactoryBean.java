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

import com.google.code.guice.repository.SimpleBatchStoreJpaRepository;
import com.google.inject.Inject;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * Overrides default one to provide {@link SimpleBatchStoreJpaRepository} instead of {@link SimpleJpaRepository}.
 * Instantiates by Guice.
 *
 * @author Alexey Krylov
 * @since 06.12.12
 */
public class CustomJpaRepositoryFactoryBean extends JpaRepositoryFactoryBean {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private CustomJpaRepositoryFactoryCreator factoryCreator;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return factoryCreator.create(entityManager);
    }
}
