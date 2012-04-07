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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public abstract class JpaRepositoryModule extends AbstractModule {

    /*===========================================[ STATIC VARIABLES ]=============*/

    public static final String P_PERSISTENCE_UNIT_NAME = "persistence-unit-name";

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
                    throw new IllegalStateException("Unable to instantiate JpaPersistenceModule: no persistence-unit-name specified");
                }
            }
        }

        this.persistenceUnitName = pUnitName;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    /**
     * Your own customizable way to provide persistence-unit name.
     * @return
     */
    protected String getPersistenceUnitName() {
        return null;
    }

    @Override
    protected void configure() {
        String moduleName = getClass().getSimpleName();
        logger.info(String.format("Configuring %s with persistence-unit name: [%s]", moduleName, persistenceUnitName));
        Properties props = getPersistenceUnitProperties();

        JpaPersistModule module = new JpaPersistModule(persistenceUnitName);
        if (props != null) {
            // Передаем параметры инициализации Persistence-контекста
            module.properties(props);
        }
        install(module);

        bind(JpaInitializer.class).asEagerSingleton();
        bind(DomainClassResolver.class).in(Scopes.SINGLETON);
        bind(CustomRepositoryImplementationResolver.class).in(Scopes.SINGLETON);
        configureRepositories();
        logger.info(String.format("%s configured", moduleName));
    }

    /**
     * Custom persistence-unit properties - for example it can be Hibernate/EclipseLink specific parameters.
     * By-default this properties loaded from file named ${persistenceUnitName}.properties.
     * @return initialized java.util.Properties.
     */
    protected Properties getPersistenceUnitProperties() {
        Properties props = new Properties();
        String propFileName = persistenceUnitName + ".properties";

        InputStream fin = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (fin != null) {
            try {
                props.load(fin);
            } catch (Exception e) {
                logger.error("Error", e);
                throw new RuntimeException("Error reading properties file: " + propFileName);
            }
        } else {
            throw new RuntimeException("Properties file not found: " + propFileName);
        }
        return props;
    }

    protected abstract void configureRepositories();

    protected Logger getLogger() {
        return logger;
    }
}
