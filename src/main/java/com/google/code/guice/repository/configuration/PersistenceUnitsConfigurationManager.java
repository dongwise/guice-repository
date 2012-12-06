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

import net.jcip.annotations.ThreadSafe;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PersistenceUnitDescriptor - TODO: description
 *
 * @author Alexey Krylov (lexx)
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

    public void registerPersistenceUnitConfiguration(PersistenceUnitConfiguration persistenceUnitConfiguration, boolean asDefault) {
        if (asDefault) {
            defaultConfiguration = persistenceUnitConfiguration;
            defaultConfiguration.setDefault(true);
        }

        configurations.put(persistenceUnitConfiguration.getName(), persistenceUnitConfiguration);
    }

    public PersistenceUnitConfiguration getPersistenceUnitConfiguration(String persistenceUnitName) {
        if (persistenceUnitName == null) {
            return defaultConfiguration;
        } else {
            return configurations.get(persistenceUnitName);
        }
    }

    public PersistenceUnitConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Collection<PersistenceUnitConfiguration> getPersistenceUnitsConfigurations() {
        return Collections.unmodifiableCollection(configurations.values());
    }
}
