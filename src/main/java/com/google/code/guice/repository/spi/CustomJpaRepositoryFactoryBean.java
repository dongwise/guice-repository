/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.spi;

import com.google.inject.Inject;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
* CustomJpaRepositoryFactoryBean - TODO: description
*
* @author Alexey Krylov (AleX)
* @since 06.12.12
*/
public class CustomJpaRepositoryFactoryBean extends JpaRepositoryFactoryBean {

    /*===========================================[ CLASS METHODS ]==============*/
    @Inject
    private CustomJpaRepositoryFactoryCreator factoryCreator;

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return factoryCreator.create(entityManager);
    }
}
