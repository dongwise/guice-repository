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
import org.springframework.util.Assert;

/**
 * RepositoryBindingImpl - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 07.12.12
 */
public class AccessibleRepositoryBinding implements RepositoryBinding {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]===========*/
    private String persistenceUnitName;
    private Class repositoryClass;
    private Class customRepositoryClass;
/*===========================================[ CONSTRUCTORS ]=================*/

    protected AccessibleRepositoryBinding(Class repositoryClass) {
        this.repositoryClass = repositoryClass;
    }
    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public RepositoryBinding withCustomImplementation(Class customRepositoryClass) {
        this.customRepositoryClass = customRepositoryClass;
        return this;
    }

    @Override
    public RepositoryBinding attachedTo(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        return this;
    }

    public Class getRepositoryClass() {
        return repositoryClass;
    }

    public Class getCustomRepositoryClass() {
        return customRepositoryClass;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }
}
