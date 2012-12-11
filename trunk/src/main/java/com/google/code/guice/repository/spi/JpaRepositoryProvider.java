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
import com.google.code.guice.repository.configuration.RepositoryBinding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;

import javax.persistence.EntityManager;

/**
 * Provides an instance of JpaRepository. Used in {@link JpaRepositoryModule#configure()}
 *
 * @author Alexey Krylov
 * @since 10.04.2012
 */
@SuppressWarnings({"SynchronizeOnThis", "FieldAccessedSynchronizedAndUnsynchronized"})
@ThreadSafe
public class JpaRepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JpaRepositoryProvider.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private RepositoryBinding binding;
    private String persistenceUnitName;

    private Class<R> repositoryClass;
    private Class customImplementationClass;

    private PersistenceUnitsConfigurationManager configurationManager;
    private Injector injector;
    private Class domainClass;
    private ApplicationContext context;

    private volatile R repository;

    /*===========================================[ CONSTRUCTORS ]===============*/

    /**
     * @param binding             repository binding parameters container
     * @param persistenceUnitName persistence unit
     */
    public JpaRepositoryProvider(RepositoryBinding binding, String persistenceUnitName) {
        this.binding = binding;
        this.persistenceUnitName = persistenceUnitName;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector,
                     CustomRepositoryImplementationResolver implementationResolver,
                     ApplicationContext context,
                     PersistenceUnitsConfigurationManager configurationManager) {

        repositoryClass = binding.getRepositoryClass();
        customImplementationClass = binding.getCustomRepositoryClass();

        this.injector = injector;
        domainClass = TypeUtil.getFirstTypeParameterClass(repositoryClass);
        this.context = context;

        if (customImplementationClass == null) {
            customImplementationClass = implementationResolver.resolve(repositoryClass);
        }

        if (customImplementationClass != null) {
            logger.info(String.format("Custom repository implementation class for [%s] set to [%s]", repositoryClass.getName(), customImplementationClass.getName()));
        }
        this.configurationManager = configurationManager;
    }

    @Override
    public R get() {
        // double-checked locking with volatile
        R repo = repository;
        if (repo == null) {
            synchronized (this) {
                repo = repository;
                if (repo == null) {
                    JpaRepositoryFactoryBean jpaRepositoryFactoryBean = createJpaRepositoryFactoryBean();
                    PersistenceUnitConfiguration configuration = configurationManager.getConfiguration(persistenceUnitName);

                    EntityManager entityManager = configuration.getEntityManager();
                    // Needs to be set first
                    jpaRepositoryFactoryBean.setTransactionManager(configuration.getTransactionManagerName());
                    // Attaching to Spring's context
                    jpaRepositoryFactoryBean.setBeanFactory(context);
                    jpaRepositoryFactoryBean.setEntityManager(entityManager);
                    jpaRepositoryFactoryBean.setRepositoryInterface(repositoryClass);
                    jpaRepositoryFactoryBean.setNamedQueries(binding.getNamedQueries());
                    jpaRepositoryFactoryBean.setQueryLookupStrategyKey(binding.getQueryLookupStrategyKey());

                    if (customImplementationClass != null) {
                        Object customRepositoryImplementation = instantiateCustomRepository(entityManager);

                        if (customRepositoryImplementation != null) {
                            jpaRepositoryFactoryBean.setCustomImplementation(customRepositoryImplementation);
                        }
                    }

                    jpaRepositoryFactoryBean.afterPropertiesSet();
                    repository = (R) jpaRepositoryFactoryBean.getObject();

                    repo = repository;
                }
            }
        }
        return repo;
    }

    /**
     * Some instantiation example is <a href="https://jira.springsource.org/browse/DATAJPA-69">here</a>.
     */
    protected JpaRepositoryFactoryBean createJpaRepositoryFactoryBean() {
        return injector.getInstance(CustomJpaRepositoryFactoryBean.class);
    }

    protected Object instantiateCustomRepository(EntityManager entityManager) {
        Object customRepositoryImplementation = null;
        /**
         * Watching a case when Repository implementation is a SimpleJpaRepository subclass -
         * we need to call a constructor with some parameters.
         */
        try {
            if (SimpleJpaRepository.class.isAssignableFrom(customImplementationClass)) {
                customRepositoryImplementation = customImplementationClass.getConstructor(Class.class, EntityManager.class).newInstance(domainClass, entityManager);
                injector.injectMembers(customRepositoryImplementation);
            } else {
                customRepositoryImplementation = injector.getInstance(customImplementationClass);
            }
        } catch (Exception e) {
            logger.error(String.format("Unable to instantiate custom repository implementation. Repository class is [%s]", customImplementationClass.getName()), e);
        }
        return customRepositoryImplementation;
    }
}