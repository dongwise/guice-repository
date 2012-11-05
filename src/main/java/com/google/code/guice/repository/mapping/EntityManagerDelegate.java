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
import net.jcip.annotations.ThreadSafe;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * Special thread-safe implementation of {@link EntityManager} - it delegates all method call's to {@link
 * ThreadLocalEntityManagerProvider}.
 *
 * @author Alexey Krylov
 * @version 1.0.1
 * @since 31.10.2012
 */
@SuppressWarnings({"ClassWithTooManyMethods", "EqualsWhichDoesntCheckParameterClass"})
@ThreadSafe
public class EntityManagerDelegate implements EntityManager {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public EntityManagerDelegate(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    public void persist(Object entity) {
        entityManagerProvider.get().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return entityManagerProvider.get().merge(entity);
    }

    @Override
    public void remove(Object entity) {
        entityManagerProvider.get().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return entityManagerProvider.get().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return entityManagerProvider.get().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return entityManagerProvider.get().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return entityManagerProvider.get().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return entityManagerProvider.get().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        entityManagerProvider.get().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        entityManagerProvider.get().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return entityManagerProvider.get().getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        entityManagerProvider.get().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManagerProvider.get().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        entityManagerProvider.get().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        entityManagerProvider.get().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        entityManagerProvider.get().refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManagerProvider.get().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        entityManagerProvider.get().clear();
    }

    @Override
    public void detach(Object entity) {
        entityManagerProvider.get().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return entityManagerProvider.get().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return entityManagerProvider.get().getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        entityManagerProvider.get().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return entityManagerProvider.get().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        EntityManager entityManager = entityManagerProvider.get();
//        System.out.println("entityManager = " + entityManager+", open: "+entityManager.isOpen()+", thread: "+ Thread.currentThread().getName());
        if (!entityManager.isOpen()) {
            try {
                entityManagerProvider.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        System.out.println("delegate: "+entityManagerProvider.getClass().getName());
        return entityManager.createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return entityManagerProvider.get().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return entityManagerProvider.get().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return entityManagerProvider.get().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return entityManagerProvider.get().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return entityManagerProvider.get().createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return entityManagerProvider.get().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return entityManagerProvider.get().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public void joinTransaction() {
        entityManagerProvider.get().joinTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return entityManagerProvider.get().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return entityManagerProvider.get().getDelegate();
    }

    @Override
    public void close() {
        entityManagerProvider.get().close();
    }

    @Override
    public boolean isOpen() {
        return entityManagerProvider.get().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return entityManagerProvider.get().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerProvider.get().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManagerProvider.get().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return entityManagerProvider.get().getMetamodel();
    }

    @Override
    public boolean equals(Object obj) {
        return entityManagerProvider.get().equals(obj);
    }

    @Override
    public int hashCode() {
        return entityManagerProvider.get().hashCode();
    }
}
