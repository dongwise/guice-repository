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

import com.google.code.guice.repository.mapping.EntityManagerFactoryProvider;
import com.google.code.guice.repository.mapping.EntityManagerProvider;
import com.google.code.guice.repository.support.CompositeTransactionInterceptor;
import com.google.code.guice.repository.support.CustomRepositoryImplementationResolver;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
//TODO: think about WS usecase and PersistFilter

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
    public static final String PERSISTENCE_UNIT_SPLIT_REGEX = ",|;";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Logger logger;
    private String[] persistenceUnits;

    /*===========================================[ CONSTRUCTORS ]===============*/

    protected JpaRepositoryModule(String... persistenceUnits) {
        logger = LoggerFactory.getLogger(getClass());
        String[] foundPersistenceUnits = null;
        if (persistenceUnits.length > 0) {
            foundPersistenceUnits = Arrays.copyOf(persistenceUnits, persistenceUnits.length);
        } else {
            Collection<String> cPersistenceUnits = getPersistenceUnits();
            if (cPersistenceUnits == null) {
                String pPersistenceUnits = System.getProperty(P_PERSISTENCE_UNITS);
                if (pPersistenceUnits == null) {
                    throw new IllegalStateException("Unable to instantiate JpaRepositoryModule: no persistence-unit-name specified");
                } else {
                    String[] splittedPersistenceUnits = pPersistenceUnits.split(PERSISTENCE_UNIT_SPLIT_REGEX);
                    if (splittedPersistenceUnits.length > 0) {
                        foundPersistenceUnits = splittedPersistenceUnits;
                    }
                }
            } else {
                foundPersistenceUnits = cPersistenceUnits.toArray(new String[cPersistenceUnits.size()]);
            }
        }

        this.persistenceUnits = foundPersistenceUnits;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    /**
     * A way to provide persistence-unit name.
     *
     * @return persistence-unit name.
     */
    protected Collection<String> getPersistenceUnits() {
        return null;
    }

    @Override
    protected void configure() {
        String simpleName = getClass().getSimpleName();
        String moduleName = simpleName.isEmpty() ? getClass().getName() : simpleName;
        logger.info(String.format("Configuring [%s] for persistence units: [%s]", moduleName, Arrays.asList(persistenceUnits)));

        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class).in(Scopes.SINGLETON);
        bind(EntityManager.class).toProvider(EntityManagerProvider.class).in(Scopes.SINGLETON);

        bind(CustomRepositoryImplementationResolver.class).in(Scopes.SINGLETON);

        PersistenceUnitsConfigurationManager configurationManager = createPersistenceUnitsConfigurationManager(persistenceUnits);
        bind(PersistenceUnitsConfigurationManager.class).toInstance(configurationManager);

        // Initializing Spring's Context
        ApplicationContext applicationContext = createApplicationContext(configurationManager.getPersistenceUnitsConfigurations());
        bind(ApplicationContext.class).toInstance(applicationContext);

        // Only Spring's @Transactional annotation is supported
        //TODO: transactionInterceptor provider relative to persistence unit??
        // Setting default Transaction Manager
        CompositeTransactionInterceptor compositeTransactionInterceptor = new CompositeTransactionInterceptor();
        requestInjection(compositeTransactionInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), compositeTransactionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), compositeTransactionInterceptor);

        configureRepositories();
        logger.info(String.format("[%s] configured", moduleName));
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
    protected ApplicationContext createApplicationContext(Collection<PersistenceUnitConfiguration> persistenceUnits) {
        GenericApplicationContext context = new GenericApplicationContext();
        // TODO http://blog.springsource.org/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/#comment-198835
        //TODO customization for dialect & etc
        // TODO: https://github.com/SpringSource/spring-data-jpa/blob/master/src/test/resources/multiple-entity-manager-integration-context.xml
        String abstractEMFBeanName = "abstractEntityManagerFactory";
        context.registerBeanDefinition(abstractEMFBeanName, BeanDefinitionBuilder.
                genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class).
                //addPropertyReference("jpaVendorAdapter", "jpaVendorAdapter").
                        //addPropertyReference("jpaDialect", "jpaDialect").
                        //addPropertyReference("dataSource", "dataSource").
                        //addPropertyReference("jpaProperties", "jpaProperties").
                        //addPropertyValue("persistenceXmlLocation", "classpath:META-INF/persistence.xml").
                        setAbstract(true).getBeanDefinition());

        AnnotationTransactionAttributeSource tas = new AnnotationTransactionAttributeSource(new SpringTransactionAnnotationParser());

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

            //TODO register first/default with name transactionManager, second - with persistenceUnitName


            PlatformTransactionManager transactionManager = context.getBean(transactionManagerName, PlatformTransactionManager.class);
            TransactionInterceptor transactionInterceptor = new TransactionInterceptor(transactionManager, tas);
            transactionInterceptor.setBeanFactory(context);

            // Default bindings for EMF & Transaction Manager - they needed for repositories with @Transactional without value
            // Wiring components
            EntityManagerFactory emf = context.getBean(entityManagerFactoryName, EntityManagerFactory.class);
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(emf, props);
            configuration.setEntityManager(entityManager);
            configuration.setEntityManagerFactory(emf);
            configuration.setTransactionManager(transactionManager);
            configuration.setTransactionManagerName(transactionManagerName);
            configuration.setTransactionInterceptor(transactionInterceptor);

            if (configuration.isDefault()) {
                //todo check if needed for transactional tests
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
    protected abstract void configureRepositories();

    protected Logger getLogger() {
        return logger;
    }
}