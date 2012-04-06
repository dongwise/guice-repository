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

package org.guice.repository;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;

public class JpaRepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JpaRepositoryProvider.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Class<? extends Repository> repositoryClass;
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private ApplicationContext context;
    private Provider<EntityManager> entityManagerProvider;
    private Class domainClass;
    private CustomRepositoryImplementationResolver customRepositoryImplementationResolver;
    private Class implementationClass;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public JpaRepositoryProvider(Class<? extends R> repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    public JpaRepositoryProvider(Class<? extends Repository> repositoryClass, Class implementationClass) {
        this.repositoryClass = repositoryClass;
        this.implementationClass = implementationClass;
    }

    public JpaRepositoryProvider() {
    }
    //TODO: возможность указания своего собственного customRepositoryClass

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector,
                     Provider<EntityManagerFactory> entityManagerFactoryProvider,
                     Provider<EntityManager> entityManagerProvider,
                     CustomRepositoryImplementationResolver customRepositoryImplementationResolver,
                     DomainClassResolver domainClassResolver) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
        if (repositoryClass == null) {
            repositoryClass = extractRepositoryClass(injector);
        }
        domainClass = domainClassResolver.resolve(repositoryClass);
        context = createSpringContext();
        this.customRepositoryImplementationResolver = customRepositoryImplementationResolver;
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
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerFactory entityManagerFactory = entityManagerFactoryProvider.get();

        // Setup new ThreadLocal entityManager instance
        GuiceLocalEntityManagerFactoryBean entityManagerFactoryBean = context.getBean(GuiceLocalEntityManagerFactoryBean.class);

        EntityManagerFactory proxiedEmf = entityManagerFactoryBean.getObject();
        if (TransactionSynchronizationManager.hasResource(proxiedEmf)) {
            TransactionSynchronizationManager.unbindResource(proxiedEmf);
        }

        TransactionSynchronizationManager.bindResource(proxiedEmf, new EntityManagerHolder(entityManagerProvider.get()));

        /**
         * Some instantiation example is here:
         * https://jira.springsource.org/browse/DATAJPA-69
         */
        JpaRepositoryFactoryBean factory = new JpaRepositoryFactoryBean() {
            protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
                return new CustomJpaRepositoryFactory(entityManager);
            }
        };

        factory.setBeanFactory(context);
        factory.setEntityManager(entityManager);
        factory.setRepositoryInterface(repositoryClass);

        implementationClass = customRepositoryImplementationResolver.resolve(repositoryClass, domainClass);
        if (implementationClass != null) {
            Object customRepositoryImplementation = null;
            //
            try {
                if (ClassUtils.isAssignable(SimpleJpaRepository.class, implementationClass)) {
                    customRepositoryImplementation = implementationClass.getConstructor(Class.class, EntityManager.class).newInstance(domainClass, entityManager);
                } else {
                    customRepositoryImplementation = implementationClass.newInstance();
                }
            } catch (Throwable e) {
                logger.error(String.format("Unable to instantiate custom repository implementation. Class is [%s]", implementationClass.getName()), e);
            }

            if (customRepositoryImplementation!=null){
                factory.setCustomImplementation(customRepositoryImplementation);
            }
        }
        factory.afterPropertiesSet();

        return (R) factory.getObject();
    }

    private static class CustomJpaRepositoryFactory extends JpaRepositoryFactory {
        private CustomJpaRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected JpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
            Class<?> repositoryInterface = metadata.getRepositoryInterface();
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainClass());

            if (isQueryDslExecutor(repositoryInterface)) {
                return new QueryDslJpaRepository(entityInformation, entityManager);
            } else {
                return new SimpleBatchStoreJpaRepository(entityInformation, entityManager);
            }
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
                return QueryDslJpaRepository.class;
            } else {
                return SimpleBatchStoreJpaRepository.class;
            }
        }

        @SuppressWarnings({"MethodOverridesPrivateMethodOfSuperclass"})
        private boolean isQueryDslExecutor(Class<?> repositoryInterface) {
            return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
        }
    }
}