/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.spi;

import com.google.inject.MembersInjector;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Field;

/**
 * EntityManagerFactoryMembersInjector - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public class EntityManagerFactoryMembersInjector<T> implements MembersInjector<T> {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private Field field;
    private EntityManagerFactory entityManagerFactory;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public EntityManagerFactoryMembersInjector(Field field, EntityManagerFactory entityManagerFactory) {
        this.field = field;
        this.entityManagerFactory = entityManagerFactory;
        field.setAccessible(true);
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public void injectMembers(T instance) {
        try {
            field.set(instance, entityManagerFactory);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}