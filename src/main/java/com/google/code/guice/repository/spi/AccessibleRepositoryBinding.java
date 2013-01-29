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

import com.google.code.guice.repository.configuration.RepositoryBinding;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * Implementation of {@link RepositoryBinding} with protected setters used by {@link
 * DefaultRepositoryBindingBuilder#build()}.
 *
 * @author Alexey Krylov
 * @since 11.12.12
 */
public class AccessibleRepositoryBinding implements RepositoryBinding {

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    private Class repositoryClass;
    private Class customRepositoryClass;
    private NamedQueries namedQueries;
    private QueryLookupStrategy.Key key;
    private String persistenceUnitName;

	/*===========================================[ CONSTRUCTORS ]=================*/

    protected AccessibleRepositoryBinding(Class repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

	/*===========================================[ CLASS METHODS ]================*/

    protected void setQueryLookupStrategyKey(QueryLookupStrategy.Key key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccessibleRepositoryBinding)) {
            return false;
        }

        AccessibleRepositoryBinding repositoryBinding = (AccessibleRepositoryBinding) o;

        if (customRepositoryClass != null ? !customRepositoryClass.equals(repositoryBinding.customRepositoryClass) : repositoryBinding.customRepositoryClass != null) {
            return false;
        }
        if (persistenceUnitName != null ? !persistenceUnitName.equals(repositoryBinding.persistenceUnitName) : repositoryBinding.persistenceUnitName != null) {
            return false;
        }
        if (repositoryClass != null ? !repositoryClass.equals(repositoryBinding.repositoryClass) : repositoryBinding.repositoryClass != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = repositoryClass != null ? repositoryClass.hashCode() : 0;
        result = 31 * result + (customRepositoryClass != null ? customRepositoryClass.hashCode() : 0);
        result = 31 * result + (persistenceUnitName != null ? persistenceUnitName.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessibleRepositoryBinding");
        sb.append("{repositoryClass=").append(repositoryClass);
        sb.append(", customRepositoryClass=").append(customRepositoryClass);
        sb.append(", persistenceUnitName='").append(persistenceUnitName).append('\'');
        sb.append('}');
        return sb.toString();
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    public Class getRepositoryClass() {
        return repositoryClass;
    }

    @Override
    public Class getCustomRepositoryClass() {
        return customRepositoryClass;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public NamedQueries getNamedQueries() {
        return namedQueries;
    }

    @Override
    public QueryLookupStrategy.Key getQueryLookupStrategyKey() {
        return key;
    }

	/*===========================================[ GETTER/SETTER ]================*/

    protected void setCustomRepositoryClass(Class customRepositoryClass) {
        this.customRepositoryClass = customRepositoryClass;
    }

    protected void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    protected void setNamedQueries(NamedQueries namedQueries) {
        this.namedQueries = namedQueries;
    }
}
