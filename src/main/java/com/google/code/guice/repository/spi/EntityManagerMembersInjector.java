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

package com.google.code.guice.repository.spi;

import com.google.inject.MembersInjector;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;

/**
 * EntityManager injector for fields, annotated with {@link PersistenceContext} with {@link
 * PersistenceContext#unitName()}:
 * <pre>
 *    {@literal @}PersistenceContext(unitName="persistence-unit1")
 *     private EntityManager entityManager;
 * </pre>
 *
 * @author Alexey Krylov
 * @since 07.12.12
 */
public class EntityManagerMembersInjector<T> implements MembersInjector<T> {

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    private Field field;
    private EntityManager entityManager;

	/*===========================================[ CONSTRUCTORS ]=================*/

    protected EntityManagerMembersInjector(Field field, EntityManager entityManager) {
        this.field = field;
        this.entityManager = entityManager;
        field.setAccessible(true);
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    public void injectMembers(T instance) {
        try {
            field.set(instance, entityManager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
