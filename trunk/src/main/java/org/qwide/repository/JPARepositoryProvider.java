/**
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

package org.qwide.repository;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JPARepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JPARepositoryProvider.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Class<? extends R> repositoryClass;
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private ApplicationContext context;
    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector, Provider<EntityManagerFactory> entityManagerFactoryProvider, Provider<EntityManager> entityManagerProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
        repositoryClass = extractRepositoryClass(injector);
        context = createSpringContext();
    }

    protected Class<? extends R> extractRepositoryClass(Injector injector) {
        Class repositoryClass;
        Key<R> key = null;
        BiMap<Key<?>, Binding<?>> biMap = ImmutableBiMap.copyOf(injector.getBindings());
        String currentProvider = toString();
        for (Binding<?> value : biMap.values()) {
            if (value.getProvider().toString().equals(currentProvider)) {
                key = (Key<R>) biMap.inverse().get(value);
                break;
            }
        }

        if (key == null) {
            throw new IllegalStateException(String.format("Unable to find provider binding for [%s]", toString()));
        }

        repositoryClass = key.getTypeLiteral().getRawType();
        return repositoryClass;
    }

    protected ApplicationContext createSpringContext() {
        GenericApplicationContext context = new GenericApplicationContext();

        context.registerBeanDefinition("entityManagerFactory",
                BeanDefinitionBuilder.genericBeanDefinition(GuiceLocalEntityManagerFactoryBean.class).
                        addConstructorArgValue(entityManagerFactoryProvider).getBeanDefinition());

        context.registerBeanDefinition("transactionManager",
                BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).getBeanDefinition());


        context.registerBeanDefinition("jpaRepositoryFactory",
                BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class).getBeanDefinition());

        return context;
    }

    public R get() {
        System.out.println("GET & BIND "+ hashCode());
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerFactory entityManagerFactory = entityManagerFactoryProvider.get();

        // Setup new ThreadLocal entityManager instance
        GuiceLocalEntityManagerFactoryBean entityManagerFactoryBean = context.getBean(GuiceLocalEntityManagerFactoryBean.class);
        EntityManagerFactory proxiedEmf = entityManagerFactoryBean.getObject();
        if (TransactionSynchronizationManager.hasResource(proxiedEmf)) {
            TransactionSynchronizationManager.unbindResource(proxiedEmf);
        }

        TransactionSynchronizationManager.bindResource(proxiedEmf, new EntityManagerHolder(entityManagerProvider.get()));


        logger.info(String.format("Accessing.get: factory=[%d], em=[%d]", entityManagerFactory.hashCode(), entityManager.hashCode()));

        //TODO: for custom implementation
//        bean.setCustomImplementation();

        //TODO: EM закрывается по первой транзакции и не пересоздается....

        /**
         * Some instantiation example is here:
         * https://jira.springsource.org/browse/DATAJPA-69
         */
        JpaRepositoryFactoryBean factory = new JpaRepositoryFactoryBean();
        factory.setEntityManager(entityManager);
        factory.setRepositoryInterface(repositoryClass);
        factory.setBeanFactory(context);
        factory.afterPropertiesSet();

        return (R) factory.getObject();
    }
}