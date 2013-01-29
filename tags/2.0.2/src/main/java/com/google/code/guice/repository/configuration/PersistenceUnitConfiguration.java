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

package com.google.code.guice.repository.configuration;

import com.google.code.guice.repository.filter.PersistFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * Represents persistence unit configuration with all related environment.
 * Configurations created in {@link JpaRepositoryModule#createPersistenceUnitsConfigurationManager(String...)} and
 * managed by {@link PersistenceUnitsConfigurationManager}.
 *
 * @author Alexey Krylov
 * @see PersistenceUnitsConfigurationManager
 * @since 06.12.12
 */
public class PersistenceUnitConfiguration {

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Properties properties;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private PlatformTransactionManager transactionManager;
    private String transactionManagerName;
    private boolean isDefault;
    private TransactionInterceptor transactionInterceptor;

	/*===========================================[ CONSTRUCTORS ]=================*/

    public PersistenceUnitConfiguration(String persistenceUnitName, Properties properties) {
        this.persistenceUnitName = persistenceUnitName;
        this.properties = (Properties) properties.clone();
    }

	/*===========================================[ CLASS METHODS ]================*/

    public Properties getProperties() {
        return (Properties) properties.clone();
    }

    public boolean isDefault() {
        return isDefault;
    }

    protected void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Represents current configuration as an EntityManager proxy. This is required for cases where
     * Configuration's EM can be changed at runtime - such as in Web-environments (see {@link PersistFilter}.
     *
     * @return proxy with EntityManager interface bound to current EM instance
     *
     * @see PersistenceUnitsConfigurationManager#changeEntityManager(String, EntityManager)
     * @see PersistFilter
     */
    public EntityManager asEntityManagerProxy() {
        return (EntityManager) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{EntityManager.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke(getEntityManager(), args);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t != null) {
                        throw t;
                    } else {
                        throw e;
                    }
                }
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PersistenceUnitConfiguration)) {
            return false;
        }

        PersistenceUnitConfiguration persistenceUnitConfiguration = (PersistenceUnitConfiguration) obj;

        return !(persistenceUnitName != null ? !persistenceUnitName.equals(persistenceUnitConfiguration.persistenceUnitName) : persistenceUnitConfiguration.persistenceUnitName != null);
    }

    @Override
    public int hashCode() {
        return persistenceUnitName != null ? persistenceUnitName.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersistenceUnitConfiguration");
        sb.append("{persistenceUnitName='").append(persistenceUnitName).append('\'');
        sb.append(", isDefault=").append(isDefault);
        sb.append(", transactionManagerName='").append(transactionManagerName).append('\'');
        sb.append(", properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }

	/*===========================================[ GETTER/SETTER ]================*/

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    protected void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public String getTransactionManagerName() {
        return transactionManagerName;
    }

    protected void setTransactionManagerName(String transactionManagerName) {
        this.transactionManagerName = transactionManagerName;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    protected void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public TransactionInterceptor getTransactionInterceptor() {
        return transactionInterceptor;
    }

    protected void setTransactionInterceptor(TransactionInterceptor transactionInterceptor) {
        this.transactionInterceptor = transactionInterceptor;
    }
}