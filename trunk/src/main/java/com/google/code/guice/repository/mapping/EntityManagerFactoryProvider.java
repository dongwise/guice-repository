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

package com.google.code.guice.repository.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManagerFactory;

/**
 * EntityManagerFactoryProvider -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
//@Singleton
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