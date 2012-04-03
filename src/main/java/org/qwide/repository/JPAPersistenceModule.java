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

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public abstract class JPAPersistenceModule extends AbstractModule {

    /*===========================================[ STATIC VARIABLES ]=============*/

    public static final String P_PERSISTENCE_UNIT_NAME = "persistence-unit-name";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Logger logger;
    private String jpaUnitName;

    /*===========================================[ CONSTRUCTORS ]===============*/

    protected JPAPersistenceModule(String... jpaUnitName) {
        if (jpaUnitName.length > 0) {
            this.jpaUnitName = jpaUnitName[0];
        } else {
            this.jpaUnitName = System.getProperty(P_PERSISTENCE_UNIT_NAME);
            if (this.jpaUnitName == null) {
                throw new IllegalStateException("Unable to instantiate JPAPersistenceModule: no jpaUnitName specified");
            }
        }

        logger = LoggerFactory.getLogger(getClass());
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected void configure() {
        logger.info(String.format("Configuring persistence with JPA unit name: [%s]", jpaUnitName));

        Properties props = new Properties();
        String propFileName = jpaUnitName + ".properties";

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

        JpaPersistModule module = new JpaPersistModule(jpaUnitName);
        // Передаем параметры инициализации Persistence-контекста
        module.properties(props);
        install(module);

        bind(JPAInitializer.class).asEagerSingleton();
        configureRepositories();
        logger.info("Persistence configured");
    }

    protected abstract void configureRepositories();

    protected Logger getLogger() {
        return logger;
    }
}
