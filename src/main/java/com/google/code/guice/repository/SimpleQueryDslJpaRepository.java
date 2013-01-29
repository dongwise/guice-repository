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

package com.google.code.guice.repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Implementation of Repository with <a href="http://www.querydsl.com/">QueryDSL</a> support.
 * guice-repository. This class constructed by Guice with <a href="http://code.google.com/p/google-guice/wiki/AssistedInject">assisted-inject</a>
 * extension - it's possible to bind {@link MethodInterceptor} for this class/extensions.
 *
 * @author Alexey Krylov
 * @since 09.12.12
 */
public class SimpleQueryDslJpaRepository<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> {

	/*===========================================[ CONSTRUCTORS ]=================*/

    @Inject
    public SimpleQueryDslJpaRepository(@Assisted JpaEntityInformation entityInformation, @Assisted EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
}
