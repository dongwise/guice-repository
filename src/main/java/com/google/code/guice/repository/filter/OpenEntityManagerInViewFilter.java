/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.filter;

import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * OpenEntityManagerInViewFilter - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public class OpenEntityManagerInViewFilter extends org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter {

    /*===========================================[ INSTANCE VARIABLES ]===========*/
    //TODO: move to extensions project with spring-web dependency
    //TODO: need real sample & Guice-init for this filter
    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected EntityManagerFactory lookupEntityManagerFactory() {
        return configurationManager.getPersistenceUnitConfiguration(getPersistenceUnitName()).getEntityManagerFactory();
    }

    //TODO transactions for web and filter is unnesessary?
    @Override
    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        return super.createEntityManager(emf);
    }
}