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

import com.google.code.guice.repository.configuration.PersistenceUnitConfiguration;
import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.code.guice.repository.configuration.ScanningJpaRepositoryModule;
import com.google.code.guice.repository.support.CustomJpaRepositoryFactory;
import com.google.code.guice.repository.support.CustomRepositoryImplementationResolver;
import com.google.code.guice.repository.support.TypeUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.*;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

/**
 * Creates an instance of JpaRepository. Bind all your repositories to this provider. Examples:
 * <pre>
 *  bind(UserRepository.class).toProvider(new JpaRepositoryProvider&lt;UserRepository&gt;());
 *  bind(AccountRepository.class).toProvider(new JpaRepositoryProvider&lt;AccountRepository&gt;());
 * </pre>
 * In case when custom implementation class is a SimpleXXXRepository subclass - it should be passed as a constructor
 * parameter.
 * <pre>
 *  bind(CustomerRepository.class).toProvider(new JpaRepositoryProvider&lt;CustomerRepository&gt;(CustomerRepositoryImpl.class));
 * </pre>
 * Note, that all Repositories is not thread-safe, because they works directly with EntityManager, which is also not
 * thread-safe. Here is a valid example for concurrent Repository usage:
 * <pre>
 *     &#64;Inject
 *     private Provider&lt;UserRepository&gt; userRepositoryProvider;
 *     ...
 *     // this method can be used concurrently
 *     public void threadSafeRepoUsageMethod(){
 *         UserRepository repo = userRepositoryProvider.get();
 *         repo.save(new User("login", "password"));
 *         repo.someOtherRepoMethod();
 *     }
 * </pre>
 *
 * @author Alexey Krylov
 * @version 1.0.0
 * @since 10.04.2012
 */
@SuppressWarnings({"SynchronizeOnThis", "FieldAccessedSynchronizedAndUnsynchronized"})
@ThreadSafe
public class JpaRepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JpaRepositoryProvider.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Class<R> repositoryClass;
    private String persistenceUnitName;
    private Class domainClass;
    private Class customImplementationClass;

    private ApplicationContext context;
    private volatile R repository;
    private PersistenceUnitsConfigurationManager persistenceUnitsConfigurationManager;

    /*===========================================[ CONSTRUCTORS ]===============*/


    /**
     * Default constructor.
     */
    public JpaRepositoryProvider() {
    }

    public JpaRepositoryProvider(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * Constructor for auto-binding.
     *
     * @param repositoryClass           class of Repository.
     * @param customImplementationClass custom implementation class of Repository.
     *
     * @see ScanningJpaRepositoryModule
     */
    public JpaRepositoryProvider(Class<R> repositoryClass, Class customImplementationClass, String persistenceUnitName) {
        Assert.notNull(repositoryClass);
        this.repositoryClass = repositoryClass;
        this.customImplementationClass = customImplementationClass;
        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * @param customImplementationClass custom implementation class of Repository. Can't be null.
     */
    public JpaRepositoryProvider(Class customImplementationClass) {
        Assert.notNull(customImplementationClass);
        this.customImplementationClass = customImplementationClass;
    }

    public JpaRepositoryProvider(Class customImplementationClass, String persistenceUnitName) {
        this(customImplementationClass);
        this.persistenceUnitName = persistenceUnitName;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector,
                     CustomRepositoryImplementationResolver customRepositoryImplementationResolver,
                     ApplicationContext context,
                     PersistenceUnitsConfigurationManager persistenceUnitsConfigurationManager) {

        if (repositoryClass == null) {
            repositoryClass = extractRepositoryClass(injector);
        }

        domainClass = TypeUtil.getFirstTypeParameterClass(repositoryClass);
        this.context = context;

        if (customImplementationClass == null) {
            customImplementationClass = customRepositoryImplementationResolver.resolve(repositoryClass);
        }

        if (customImplementationClass != null) {
            logger.info(String.format("Custom repository implementation class for [%s] set to [%s]", repositoryClass.getName(), customImplementationClass.getName()));
        }
        this.persistenceUnitsConfigurationManager = persistenceUnitsConfigurationManager;
    }

    /**
     * Extracts repository class from Guice-bindings (it's a type paramater).
     *
     * @param injector Google Guice injector.
     *
     * @return repository class.
     */
    protected Class<R> extractRepositoryClass(Injector injector) {
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

    @Override
    public R get() {
        // double-checked locking with volatile
        R repo = repository;
        if (repo == null) {
            synchronized (this) {
                repo = repository;
                if (repo == null) {
                    JpaRepositoryFactoryBean jpaRepositoryFactoryBean = createJpaRepositoryFactoryBean();

                    PersistenceUnitConfiguration configuration = persistenceUnitsConfigurationManager.getPersistenceUnitConfiguration(persistenceUnitName);
                    EntityManager entityManager = configuration.getEntityManager();
                    // Attaching to Spring's context
                    jpaRepositoryFactoryBean.setBeanFactory(context);
                    jpaRepositoryFactoryBean.setEntityManager(entityManager);
                    jpaRepositoryFactoryBean.setRepositoryInterface(repositoryClass);
                    jpaRepositoryFactoryBean.setTransactionManager(configuration.getTransactionManagerName());

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
        return new JpaRepositoryFactoryBean() {
            @Override
            protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
                return new CustomJpaRepositoryFactory(entityManager);
            }
        };
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
            } else {
                customRepositoryImplementation = customImplementationClass.newInstance();
            }
        } catch (Throwable e) {
            logger.error(String.format("Unable to instantiate custom repository implementation. Repository class is [%s]", customImplementationClass.getName()), e);
        }
        return customRepositoryImplementation;
    }
}