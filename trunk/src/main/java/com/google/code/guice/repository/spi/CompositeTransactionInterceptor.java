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

import com.google.code.guice.repository.configuration.JpaRepositoryModule;
import com.google.code.guice.repository.configuration.PersistenceUnitConfiguration;
import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

/**
 * Interceptor for all Guice-instantiated entities with @Transactional methods. Binding process performs in {@link
 * JpaRepositoryModule#configure()}. Interceptor named Composite because it supports multiple persistence units.
 * Actual one determined from invocation method annotations. After that it delegates invocation to interceptor bound
 * to persistence unit. Interceptor, bound to persistence unit, contains all nesessary information to handle passed
 * invocation - transaction manager, transaction attribute source, bean factory and so on.
 *
 * @author Alexey Krylov
 * @see JpaRepositoryModule#createApplicationContext(PersistenceUnitsConfigurationManager)
 * @since 07.12.12
 */
public class CompositeTransactionInterceptor implements MethodInterceptor {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private PersistenceUnitsConfigurationManager configurationManager;
    private TransactionAttributeSource transactionAttributeSource;

    /*===========================================[ CONSTRUCTORS ]=================*/

    @Inject
    protected void init(PersistenceUnitsConfigurationManager configurationManager, TransactionAttributeSource transactionAttributeSource) {
        this.configurationManager = configurationManager;
        this.transactionAttributeSource = transactionAttributeSource;
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // persistence unit name detection
        Class<?> targetClass = invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        TransactionAttribute txAttr = transactionAttributeSource.getTransactionAttribute(invocation.getMethod(), targetClass);
        String persistenceUnitName = txAttr.getQualifier();

        PersistenceUnitConfiguration configuration = configurationManager.getConfiguration(persistenceUnitName);
        return configuration.getTransactionInterceptor().invoke(invocation);
    }
}