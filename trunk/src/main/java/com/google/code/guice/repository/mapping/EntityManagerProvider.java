/*
* Copyright (c) 2012, i-Free. All Rights Reserved.
*/

package com.google.code.guice.repository.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * EntityManagerProvider -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
@Singleton
public class EntityManagerProvider implements Provider<EntityManager> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(ApplicationContext context) {
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(emf, emf.getProperties());
    }

    @Override
    public EntityManager get() {
        return entityManager;
    }
}
