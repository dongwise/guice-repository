/**
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

import com.google.inject.Provider;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * @author Alexey Krylov
 * @version 1.0.1
 * @since 31.10.2012
 */
public class EntityManagerFactoryDelegate implements EntityManagerFactory {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManagerFactory delegate;
    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public EntityManagerFactoryDelegate(EntityManagerFactory delegate, Provider<EntityManager> entityManagerProvider) {
        this.delegate = delegate;
        this.entityManagerProvider = entityManagerProvider;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    public EntityManager createEntityManager() {
        return entityManagerProvider.get();
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return entityManagerProvider.get();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Cache getCache() {
        return delegate.getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return delegate.getPersistenceUnitUtil();
    }
}
