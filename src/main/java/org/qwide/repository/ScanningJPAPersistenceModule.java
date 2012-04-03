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

package org.qwide.repository;

import org.reflections.Reflections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

public class ScanningJPAPersistenceModule extends JPAPersistenceModule {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private String targetScanPackage;

    /*===========================================[ CONSTRUCTORS ]===============*/

    /**
     *
     * @param targetScanPackage package to scan for repositories.
     * @param persistenceUnitName
     */
    public ScanningJPAPersistenceModule(String targetScanPackage, String... persistenceUnitName) {
        super(persistenceUnitName);
        this.targetScanPackage = targetScanPackage;

    }
    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected void configureRepositories() {
        Reflections reflections = new Reflections(targetScanPackage);
        Set<Class<?>> repositoryClasses = new HashSet<Class<?>>();

        repositoryClasses.addAll(reflections.getTypesAnnotatedWith(Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(org.springframework.data.repository.Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(CrudRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(PagingAndSortingRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(JpaRepository.class));

        for (Class<?> repositoryClass : repositoryClasses) {
            bind(repositoryClass).toProvider(new JPARepositoryProvider(repositoryClass));
            getLogger().info(String.format("Found repository: [%s]", repositoryClass.getName()));
        }
    }
}
