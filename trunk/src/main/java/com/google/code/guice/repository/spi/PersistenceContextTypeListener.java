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

import com.google.code.guice.repository.configuration.PersistenceUnitConfiguration;
import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;

/**
 * PersistenceContextTypeListener - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 07.12.12
 */
public class PersistenceContextTypeListener implements TypeListener {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        for (Class<?> c = typeLiteral.getRawType(); !c.equals(Object.class); c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getType().equals(EntityManager.class)
                        && field.isAnnotationPresent(PersistenceContext.class)) {
                    PersistenceContext persistenceContext = field.getAnnotation(PersistenceContext.class);
                    String persistenceUnitName = persistenceContext.unitName();
                    PersistenceUnitConfiguration configuration = configurationManager.getPersistenceUnitConfiguration(persistenceUnitName);
                    typeEncounter.register(new EntityManagerMembersInjector<T>(field, configuration.getEntityManager()));
                }
            }
        }
    }
}
