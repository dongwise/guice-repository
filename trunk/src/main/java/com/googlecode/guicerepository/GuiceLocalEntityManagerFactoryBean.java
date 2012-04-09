/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.guicerepository;

import com.google.inject.Provider;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * Holder for EntityManagerFactory, it is needed for Spring-data integration.
 *
 * @author Alexey Krylov AKA lexx
 */
class GuiceLocalEntityManagerFactoryBean extends LocalEntityManagerFactoryBean {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Provider<EntityManagerFactory> entityManagerFactoryProvider;

    /*===========================================[ CONSTRUCTORS ]===============*/

    GuiceLocalEntityManagerFactoryBean(Provider<EntityManagerFactory> entityManagerFactoryProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        return entityManagerFactoryProvider.get();
    }
}