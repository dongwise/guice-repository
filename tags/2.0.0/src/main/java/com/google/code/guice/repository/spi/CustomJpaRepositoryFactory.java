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
import com.google.inject.assistedinject.Assisted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.io.Serializable;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;

/**
 * Spring-data specifics - we need this because our "base" Repository implementation is not a {@link
 * SimpleJpaRepository}, but
 * Repository with batch-store support.
 *
 * @author Alexey Krylov
 * @see SimpleBatchStoreJpaRepository
 * @since 10.04.2012
 */
public class CustomJpaRepositoryFactory extends JpaRepositoryFactory {

    private SimpleQueryDslJpaRepositoryFactory queryDslJpaRepositoryFactory;
    private SimpleBatchStoreJpaRepositoryFactory batchRepositoryFactory;

    /*===========================================[ CONSTRUCTORS ]=================*/

    @Inject
    protected CustomJpaRepositoryFactory(@Assisted EntityManager entityManager,
                                         SimpleBatchStoreJpaRepositoryFactory batchRepositoryFactory,
                                         SimpleQueryDslJpaRepositoryFactory queryDslJpaRepositoryFactory) {
        super(entityManager);
        this.batchRepositoryFactory = batchRepositoryFactory;
        this.queryDslJpaRepositoryFactory = queryDslJpaRepositoryFactory;
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected JpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

        SimpleJpaRepository repo;
        if (isQueryDslExecutor(repositoryInterface)) {
            repo = queryDslJpaRepositoryFactory.create(entityInformation, entityManager);
        } else {
            repo = batchRepositoryFactory.create(entityInformation, entityManager);
        }

        return repo;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
            return SimpleQueryDslJpaRepositoryFactory.class;
        } else {
            return SimpleBatchStoreJpaRepository.class;
        }
    }

    @SuppressWarnings({"MethodOverridesPrivateMethodOfSuperclass", "OverloadedMethodsWithSameNumberOfParameters"})
    private static boolean isQueryDslExecutor(Class<?> repositoryInterface) {
        return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
    }
}
