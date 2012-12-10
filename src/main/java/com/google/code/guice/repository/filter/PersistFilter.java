/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
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
        String persistenceUnitName = request.getParameter(P_PERSISTENCE_UNIT_NAME);
        return configurationManager.getConfiguration(persistenceUnitName).getEntityManagerFactory();
    }

    @Override
    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        EntityManager entityManager = super.createEntityManager(emf);
        configurationManager.getConfiguration(getPersistenceUnitName()).setEntityManager(entityManager);
        return entityManager;
    }
}