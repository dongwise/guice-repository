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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

/**
 * PersistenceUnitDescriptor - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 06.12.12
 */
public class PersistenceUnitConfiguration {

    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private String name;
    private Properties properties;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private String transactionManagerName;
    private boolean isDefault;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public PersistenceUnitConfiguration(String name, Properties properties) {
        this.name = name;
        this.properties = (Properties) properties.clone();
    }
    /*===========================================[ CLASS METHODS ]================*/

    public String getName() {
        return name;
    }

    public Properties getProperties() {
        return properties;
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

    public boolean isDefault() {
        return isDefault;
    }

    protected void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersistenceUnitConfiguration)) {
            return false;
        }

        PersistenceUnitConfiguration persistenceUnitConfiguration = (PersistenceUnitConfiguration) o;

        if (name != null ? !name.equals(persistenceUnitConfiguration.name) : persistenceUnitConfiguration.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersistenceUnitConfiguration");
        sb.append("{name='").append(name).append('\'');
        sb.append(", properties=").append(properties);
        sb.append(", transactionManagerName='").append(transactionManagerName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
