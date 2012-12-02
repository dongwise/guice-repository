/*
* Copyright (c) 2012, i-Free. All Rights Reserved.
*/

package com.google.code.guice.repository.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManagerFactory;

/**
 * EntityManagerFactoryProvider -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
@Singleton
public class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManagerFactory emf;

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(ApplicationContext context) {
        emf = context.getBean(EntityManagerFactory.class);
    }

    @Override
    public EntityManagerFactory get() {
        return emf;
    }
}