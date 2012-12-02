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

package com.google.code.guice.repository.configuration;

import com.google.code.guice.repository.mapping.ApplicationContextProvider;
import com.google.code.guice.repository.mapping.EntityManagerFactoryProvider;
import com.google.code.guice.repository.mapping.EntityManagerProvider;
import com.google.code.guice.repository.support.CustomRepositoryImplementationResolver;
import com.google.code.guice.repository.support.DomainClassResolver;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.InputStream;
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

    public static final String P_PERSISTENCE_UNIT_NAME = "persistence-unit-name";
    public static final String P_PERSISTENCE_UNIT_PROPERTIES = "persistence-unit-properties";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Logger logger;
    private String persistenceUnitName;

    /*===========================================[ CONSTRUCTORS ]===============*/

    protected JpaRepositoryModule(String... persistenceUnitName) {
        logger = LoggerFactory.getLogger(getClass());
        String pUnitName;
        if (persistenceUnitName.length > 0) {
            pUnitName = persistenceUnitName[0];
        } else {
            pUnitName = getPersistenceUnitName();
            if (pUnitName == null) {
                pUnitName = System.getProperty(P_PERSISTENCE_UNIT_NAME);
                if (pUnitName == null) {
                    throw new IllegalStateException("Unable to instantiate JpaRepositoryModule: no persistence-unit-name specified");
                }
            }
        }

        this.persistenceUnitName = pUnitName;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    /**
     * A way to provide persistence-unit name.
     *
     * @return persistence-unit name.
     */
    protected String getPersistenceUnitName() {
        return null;
    }

    @Override
    protected void configure() {
        String moduleName = getClass().getSimpleName();
        logger.info(String.format("Configuring %s with persistence-unit name: [%s]", moduleName, persistenceUnitName));

        Properties props = getPersistenceUnitProperties();
        bind(String.class).annotatedWith(Names.named(P_PERSISTENCE_UNIT_NAME)).toInstance(persistenceUnitName);
        bind(Properties.class).annotatedWith(Names.named(P_PERSISTENCE_UNIT_PROPERTIES)).toInstance(props);

        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class);
        bind(EntityManager.class).toProvider(EntityManagerProvider.class);

        bind(DomainClassResolver.class).in(Scopes.SINGLETON);
        bind(CustomRepositoryImplementationResolver.class).in(Scopes.SINGLETON);

        // Only Spring's @Transactional annotation is supported
        AnnotationTransactionAttributeSource tas = new AnnotationTransactionAttributeSource(new SpringTransactionAnnotationParser());
        // Transaction Manager will be configured later
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor(null, tas);
        bind(TransactionInterceptor.class).toInstance(transactionInterceptor);

        // Spring's ApplicationContext provider
        bind(ApplicationContext.class).toProvider(ApplicationContextProvider.class);

        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), transactionInterceptor);

        configureRepositories();
        logger.info(String.format("%s configured", moduleName));
    }

    /**
     * Custom persistence-unit properties - for example it can consist Hibernate/EclipseLink specific parameters.
     * By-default this properties loaded from file named ${persistence-unit-name}.properties located in the classpath.
     *
     * @return initialized java.util.Properties or null.
     */
    protected Properties getPersistenceUnitProperties() {
        Properties props = null;
        String propFileName = persistenceUnitName + ".properties";

        InputStream pStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (pStream != null) {
            props = new Properties();
            try {
                props.load(pStream);
            } catch (Exception e) {
                logger.error(String.format("Unable to load properties for persistence-unit: [%s]", persistenceUnitName), e);
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