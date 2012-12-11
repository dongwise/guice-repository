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

import com.google.code.guice.repository.filter.PersistFilter;
import com.google.code.guice.repository.spi.JpaRepositoryProvider;
import net.jcip.annotations.ThreadSafe;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents Manager of persistence units configurations. Used to register/retrieve configurations in various project
 * aspects. Manager instantiates at {@link JpaRepositoryModule#createPersistenceUnitsConfigurationManager(String...)}.
 * Each manager contains one default configuration. Default configuration used for all untargeted (without direct
 * persistence unit name specification) binds/requests.
 *
 * @author Alexey Krylov
 * @see PersistFilter
 * @see JpaRepositoryProvider
 * @see JpaRepositoryModule
 * @since 06.12.12
 */
@ThreadSafe
public class PersistenceUnitsConfigurationManager {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private Map<String, PersistenceUnitConfiguration> configurations;
    private PersistenceUnitConfiguration defaultConfiguration;

    /*===========================================[ CONSTRUCTORS ]=================*/

    protected PersistenceUnitsConfigurationManager() {
        configurations = Collections.synchronizedMap(new LinkedHashMap<String, PersistenceUnitConfiguration>());
    }

    /*===========================================[ CLASS METHODS ]================*/

    /**
     * Registers new configuration.
     *
     * @param persistenceUnitConfiguration configuration to register
     * @param asDefault                    should be set to {@code true} for registering specified configuration as
     *                                     default. Previous default configuration will be overwritten.
     */
    public void registerConfiguration(PersistenceUnitConfiguration persistenceUnitConfiguration, boolean asDefault) {
        if (asDefault) {
            if (defaultConfiguration != null) {
                defaultConfiguration.setDefault(false);
            }
            defaultConfiguration = persistenceUnitConfiguration;
            defaultConfiguration.setDefault(true);
        }

        configurations.put(persistenceUnitConfiguration.getPersistenceUnitName(), persistenceUnitConfiguration);
    }

    /**
     * Searches in registered configurations and returns configuration related to specified {@code
     * persistenceUnitName}.
     * <p>
     * <b>NOTE:</b> default configuration will always be returned for null/empty {@code persistenceUnitName}.
     * </p>
     *
     * @param persistenceUnitName name of persistence unit. Can be {@code null}.
     *
     * @return configuration related to specified {@code persistenceUnitName} or default configuration for case of
     *         null/empty parameter
     *
     * @throws IllegalStateException if specified {@code persistenceUnitName} is not null/empty, but configuration is
     *                               not registered
     */
    public PersistenceUnitConfiguration getConfiguration(String persistenceUnitName) {
        if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
            return defaultConfiguration;
        } else {
            PersistenceUnitConfiguration persistenceUnitConfiguration = configurations.get(persistenceUnitName);
            if (persistenceUnitConfiguration == null) {
                throw new IllegalStateException(String.format("Persistence configuration for [%s] is not registered", persistenceUnitName));
            }
            return persistenceUnitConfiguration;
        }
    }

    public PersistenceUnitConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Collection<PersistenceUnitConfiguration> getConfigurations() {
        return configurations.values();
    }

    /**
     * Changes EntityManager bound to specified configuration.
     *
     * @param persistenceUnitName persistence unit name
     * @param entityManager       new entity manager
     *
     * @throws IllegalArgumentException if specified {@code entityManager} is null
     * @see PersistFilter
     */
    public void changeEntityManager(String persistenceUnitName, EntityManager entityManager) {
        Assert.notNull(entityManager);
        getConfiguration(persistenceUnitName).setEntityManager(entityManager);
    }
}
