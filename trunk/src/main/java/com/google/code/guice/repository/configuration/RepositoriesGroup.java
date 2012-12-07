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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

//TODO: repositoryGroupBuilder.withExclusion/inclusionFilter

/**
 * RepositoryGroup - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 07.12.12
 */
public class RepositoriesGroup {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Collection<String> repositoriesPackages;

    /*===========================================[ CLASS METHODS ]================*/

    protected RepositoriesGroup(String repositoriesPackage, String persistenceUnitName) {
        repositoriesPackages = Arrays.asList(repositoriesPackage);
        this.persistenceUnitName = persistenceUnitName;
    }

    protected RepositoriesGroup(Collection<String> repositoriesPackages, String persistenceUnitName) {
        this.repositoriesPackages = new ArrayList<String>(repositoriesPackages);
        this.persistenceUnitName = persistenceUnitName;
    }

    /*===========================================[ CONSTRUCTORS ]=================*/

    public Collection<String> getRepositoriesPackages() {
        return Collections.unmodifiableCollection(repositoriesPackages);
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }
}