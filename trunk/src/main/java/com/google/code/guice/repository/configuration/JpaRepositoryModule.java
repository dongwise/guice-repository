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

import com.google.code.guice.repository.spi.JpaRepositoryProvider;
import com.google.code.guice.repository.spi.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 * Guice module with Repository support. Repository bindings should be made in <code>configureRepositories</code>
 * method. Module uses JpaPersistModule from guice-persist, which is requires persistence-unit name as an input
 * parameter. There is three options to specify persistence-unit name: <ol> <li> constructor parameter. For example:
 * new
 * JpaRepositoryModule("my-persistence-unit") </li> <li> system property 'persistence-unit-name'. For example: launch
 * an
 * application with -Dpersistence-unit-name=my-persistence-unit </li> <li>override <code>getPersistenceUnitName</code>
 * method. For example:
 * <pre>
 *          new JpaRepositoryModule(){
 *               protected String getPersistenceUnitName() {
 *                    return "my-persistence-unit";
 *               }
 *          }
 *       </pre>
 * </li> </ol>
 *
 * It is very useful to separate ORM specifics from persistence.xml. This technique gives you possibilities to pack
 * your
 * persistence.xml with mappings/specification-driven aspects to artifact. Module will look for this specifics in
 * ${persistence-unit-name}.properties file placed in the classpath. Also you can define all ORM specifics in
 * persistence.xml, there is no problem with it.
 *
 * @author Alexey Krylov
 * @version 1.0.0
 * @since 10.04.2012
 */
public abstract class JpaRepositoryModule extends AbstractModule {

    /*===========================================[ STATIC VARIABLES ]=============*/

    public static final String P_PERSISTENCE_UNITS = "persistence-units";
    public static final String PERSISTENCE_UNITS_SPLIT_REGEX = ",|;";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Logger logger;
    protected String[] persistenceUnitsNames;

    /*===========================================[ CONSTRUCTORS ]===============*/

    protected JpaRepositoryModule(String... persistenceUnitsNames) {
        logger = LoggerFactory.getLogger(getClass());
        String[] foundPersistenceUnitsNames = null;
        if (persistenceUnitsNames.length > 0) {
            foundPersistenceUnitsNames = Arrays.copyOf(persistenceUnitsNames, persistenceUnitsNames.length);
        } else {
            Collection<String> cPersistenceUnits = getPersistenceUnitsNames();
            if (cPersistenceUnits == null) {
                String pPersistenceUnits = System.getProperty(P_PERSISTENCE_UNITS);
                if (pPersistenceUnits == null) {
                    throw new IllegalStateException("Unable to instantiate JpaRepositoryModule: no persistence-unit-name specified");
                } else {
                    String[] splittedPersistenceUnits = pPersistenceUnits.split(PERSISTENCE_UNITS_SPLIT_REGEX);
                    if (splittedPersistenceUnits.length > 0) {
                        foundPersistenceUnitsNames = splittedPersistenceUnits;
                    }
                }
            } else {
                foundPersistenceUnitsNames = cPersistenceUnits.toArray(new String[cPersistenceUnits.size()]);
            }
        }

        this.persistenceUnitsNames = foundPersistenceUnitsNames;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    /**
     * A way to provide persistence-unit name.
     *
     * @return persistence-unit name.
     */
    protected Collection<String> getPersistenceUnitsNames() {
        return null;
    }

    @Override
    protected void configure() {
        String simpleName = getClass().getSimpleName();
        String moduleName = simpleName.isEmpty() ? getClass().getName() : simpleName;
        logger.info(String.format("Configuring [%s] for persistence units: %s", moduleName, Arrays.asList(persistenceUnitsNames)));

        bind(CustomRepositoryImplementationResolver.class).in(Scopes.SINGLETON);

        PersistenceUnitsConfigurationManager configurationManager = createPersistenceUnitsConfigurationManager(persistenceUnitsNames);
        bind(PersistenceUnitsConfigurationManager.class).toInstance(configurationManager);

        // Only Spring @Transactional annotation is supported
        AnnotationTransactionAttributeSource tas = new AnnotationTransactionAttributeSource(new SpringTransactionAnnotationParser());
        bind(TransactionAttributeSource.class).toInstance(tas);

        // Initializing Spring Context
        ApplicationContext applicationContext = createApplicationContext(configurationManager.getPersistenceUnitsConfigurations(), tas);
        bind(ApplicationContext.class).toInstance(applicationContext);

        // Initializing interceptor for components created by Guice and marked with @Transactional
        CompositeTransactionInterceptor transactionInterceptor = new CompositeTransactionInterceptor();
        requestInjection(transactionInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), transactionInterceptor);

        // Support for EntityManager with @PersistenceContext injections
        PersistenceContextTypeListener persistenceContextTypeListener = new PersistenceContextTypeListener();
        requestInjection(persistenceContextTypeListener);
        bindListener(Matchers.any(), persistenceContextTypeListener);

        // Support for EntityManagerFactory with @PersistenceUnit injections
        PersistenceUnitTypeListener persistenceUnitTypeListener = new PersistenceUnitTypeListener();
        requestInjection(persistenceUnitTypeListener);
        bindListener(Matchers.any(), persistenceUnitTypeListener);

        // TODO rename
        DefaultRepositoryBinder repositoryBinder = createRepositoryBinder();
        bindRepositories(repositoryBinder);

        Collection<AccessibleRepositoryBinding> bindings = repositoryBinder.getBindings();
        for (AccessibleRepositoryBinding binding : bindings) {
            Class<? extends Repository> repositoryClass = binding.getRepositoryClass();
            Class customRepositoryClass = binding.getCustomRepositoryClass();
            String persistenceUnitName = binding.getPersistenceUnitName();
            String specificPersistenceUnitName = extractAnnotationsPersistenceUnitName(repositoryClass);

            if (specificPersistenceUnitName != null && !specificPersistenceUnitName.isEmpty()) {
                persistenceUnitName = specificPersistenceUnitName;
            }

            if (configurationManager.getPersistenceUnitConfiguration(persistenceUnitName) == null) {
                throw new IllegalStateException(String.format("Unable to register repository [%s] - persistence unit [%s] is not registered",
                        repositoryClass.getName(), persistenceUnitName));
            }

            bind(repositoryClass).toProvider(new JpaRepositoryProvider(repositoryClass, customRepositoryClass, persistenceUnitName));

            logger.info(String.format("[%s] %s attached to [%s] available for injection",
                    repositoryClass.getName(),
                    customRepositoryClass != null ? "(" + customRepositoryClass.getName() + ")" : "",
                    persistenceUnitName));
        }

        logger.info(String.format("[%s] configured", moduleName));
    }

    protected String extractAnnotationsPersistenceUnitName(Class<? extends Repository> repositoryClass) {
        String persistenceUnitName = null;
        PersistenceContext persistenceContext = repositoryClass.getAnnotation(PersistenceContext.class);
        if (persistenceContext != null) {
            persistenceUnitName = persistenceContext.unitName();
        }

        boolean persistenceUnitNameFound = false;
        boolean persistenceContextBased = false;
        if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
            Transactional annotation = repositoryClass.getAnnotation(Transactional.class);
            if (annotation != null) {
                persistenceUnitName = annotation.value();
                if (!persistenceUnitName.isEmpty()) {
                    persistenceUnitNameFound = true;
                }
            }
        } else {
            persistenceUnitNameFound = true;
            persistenceContextBased = true;
        }

        if (persistenceUnitNameFound) {
            logger.info(String.format("[%s] contains specified persistenceUnitName [%s] in %s annotation",
                    repositoryClass.getName(),
                    persistenceUnitName,
                    persistenceContextBased ? "@PersistenceContext" : "@Transactional"));
        }

        return persistenceUnitName;
    }

    protected DefaultRepositoryBinder createRepositoryBinder() {
        return new DefaultRepositoryBinder();
    }

    protected PersistenceUnitsConfigurationManager createPersistenceUnitsConfigurationManager(String... persistenceUnits) {
        PersistenceUnitsConfigurationManager manager = new PersistenceUnitsConfigurationManager();
        for (int i = 0; i < persistenceUnits.length; i++) {
            String persistenceUnitName = persistenceUnits[i];
            Properties persistenceUnitProperties = getPersistenceUnitProperties(persistenceUnitName);
            // First persistence unit will be "default"
            boolean isDefaultConfiguration = i == 0;
            manager.registerPersistenceUnitConfiguration(
                    new PersistenceUnitConfiguration(persistenceUnitName, persistenceUnitProperties), isDefaultConfiguration);
        }
        return manager;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected ApplicationContext createApplicationContext(Collection<PersistenceUnitConfiguration> persistenceUnits, TransactionAttributeSource tas) {
        GenericApplicationContext context = new GenericApplicationContext();
        // TODO http://blog.springsource.org/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/#comment-198835
        //TODO customization for dialect & etc
        // TODO: https://github.com/SpringSource/spring-data-jpa/blob/master/src/test/resources/multiple-entity-manager-integration-context.xml
        String abstractEMFBeanName = "abstractEntityManagerFactory";
        context.registerBeanDefinition(abstractEMFBeanName, BeanDefinitionBuilder.
                genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class).
                //addPropertyReference("jpaVendorAdapter", "jpaVendorAdapter").
                //        addPropertyReference("jpaDialect", "jpaDialect").
                        //addPropertyValue("persistenceXmlLocation", "classpath:META-INF/persistence.xml").
                        setAbstract(true).getBeanDefinition());

        for (PersistenceUnitConfiguration configuration : persistenceUnits) {
            String persistenceUnitName = configuration.getName();
            // Naming is important - it's needed for later TransactionManager resolution based on @Transactional value with persistenceUnitName
            String transactionManagerName = persistenceUnitName;
            Properties props = configuration.getProperties();

            String entityManagerFactoryName = "entityManagerFactory#" + persistenceUnitName;

            context.registerBeanDefinition(entityManagerFactoryName,
                    BeanDefinitionBuilder.genericBeanDefinition().setParentName(abstractEMFBeanName).
                            addPropertyValue("persistenceUnitName", persistenceUnitName).
                            addPropertyValue("jpaProperties", props).getBeanDefinition());

            context.registerBeanDefinition(transactionManagerName,
                    BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).
                            addPropertyReference("entityManagerFactory", entityManagerFactoryName).getBeanDefinition());

            PlatformTransactionManager transactionManager = context.getBean(transactionManagerName, PlatformTransactionManager.class);
            TransactionInterceptor transactionInterceptor = new TransactionInterceptor(transactionManager, tas);
            transactionInterceptor.setBeanFactory(context);

            // Wiring components
            EntityManagerFactory emf = context.getBean(entityManagerFactoryName, EntityManagerFactory.class);
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(emf, props);
            configuration.setEntityManager(entityManager);
            configuration.setEntityManagerFactory(emf);
            configuration.setTransactionManager(transactionManager);
            configuration.setTransactionManagerName(transactionManagerName);
            configuration.setTransactionInterceptor(transactionInterceptor);

            // Default bindings for EMF & Transaction Manager - they needed for repositories with @Transactional without value
            if (configuration.isDefault()) {
                context.registerAlias(entityManagerFactoryName, "entityManagerFactory");
                context.registerAlias(persistenceUnitName, "transactionManager");
            }
        }

        return context;
    }

    /**
     * Custom persistence-unit properties - for example it can consist Hibernate/EclipseLink specific parameters.
     * By-default this properties loaded from file named ${persistence-unit-name}.properties located in the classpath.
     *
     * @return initialized java.util.Properties or null.
     */
    protected Properties getPersistenceUnitProperties(String persistenceUnitName) {
        Properties props = null;
        String propFileName = persistenceUnitName + ".properties";

        InputStream pStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            props = new Properties();
            if (pStream != null) {
                props.load(pStream);
            }
        } catch (Exception e) {
            logger.error(String.format("Unable to load properties for persistence-unit: [%s]", persistenceUnitName), e);
        } finally {
            if (pStream != null) {
                try {
                    pStream.close();
                } catch (IOException e) {
                    logger.error("Error", e);
                }
            }
        }
        return props;
    }

    /**
     * Bind your repositories here.
     */
    protected abstract void bindRepositories(RepositoryBinder binder);

    protected Logger getLogger() {
        return logger;
    }
}