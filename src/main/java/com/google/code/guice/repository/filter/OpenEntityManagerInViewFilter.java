/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.filter;

import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * OpenEntityManagerInViewFilter - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public class OpenEntityManagerInViewFilter extends org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter {

    /*===========================================[ STATIC VARIABLES ]=============*/

    public static final String P_PERSISTENCE_UNIT_NAME = "persistenceUnitName";

    /*===========================================[ INSTANCE VARIABLES ]===========*/
    //TODO: move to extensions project with spring-web dependency
    //TODO: need real sample & Guice-init for this filter
    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected EntityManagerFactory lookupEntityManagerFactory(HttpServletRequest request) {
        String persistenceUnitName = request.getParameter(P_PERSISTENCE_UNIT_NAME);
        return configurationManager.getConfiguration(persistenceUnitName).getEntityManagerFactory();
    }

    @Override
    protected EntityManagerFactory lookupEntityManagerFactory() {
        return configurationManager.getConfiguration(getPersistenceUnitName()).getEntityManagerFactory();
    }

    //TODO transactions for web and filter is unnesessary?
    @Override
    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        EntityManager entityManager = super.createEntityManager(emf);
        //TODO
        //configurationManager.getConfiguration(getPersistenceUnitName()).setEntityManager(entityManager);
        return entityManager;
    }
}