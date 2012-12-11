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

package com.google.code.guice.repository.filter;

import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * OpenEntityManagerInViewFilter - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public class PersistFilter extends OpenEntityManagerInViewFilter {

    /*===========================================[ STATIC VARIABLES ]=============*/

    public static final String P_PERSISTENCE_UNIT_NAME = "persistenceUnitName";

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected EntityManagerFactory lookupEntityManagerFactory(HttpServletRequest request) {
        String persistenceUnitName = getPersistenceUnitName();
        if(persistenceUnitName == null) {
            persistenceUnitName = request.getParameter(P_PERSISTENCE_UNIT_NAME);
        }
        // persistenceUnitName can still be null - in this case default configuration will be used
        return configurationManager.getConfiguration(persistenceUnitName).getEntityManagerFactory();
    }

    @Override
    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        EntityManager entityManager = super.createEntityManager(emf);
        configurationManager.changeEntityManager(getPersistenceUnitName(), entityManager);
        return entityManager;
    }
}