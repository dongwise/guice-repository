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

import com.google.code.guice.repository.BatchStoreJpaRepository;
import com.google.code.guice.repository.JpaRepositoryProvider;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.*;

import static com.google.common.collect.Collections2.filter;

/**
 * Guice module with Repository support and auto Repository-scanning abilities. With this module there is no need to
 * manual binding of any Repositories - just specify target repository package :
 * <pre>
 *     install(new ScanningJpaRepositoryModule("com.mycorp.repo"){
 *        protected String getPersistenceUnitName(){
 *            return "my-persistence-unit";
 *        }
 *     });
 * </pre>
 *
 * @author Alexey Krylov
 * @version 1.0.0
 * @since 10.04.2012
 */
public class ScanningJpaRepositoryModule extends JpaRepositoryModule {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Iterable<String> targetScanPackages;

    /*===========================================[ CONSTRUCTORS ]===============*/

    /**
     * @param targetScanPackage package to scan for repositories.
     */
    public ScanningJpaRepositoryModule(String targetScanPackage, String... persistenceUnitName) {
        super(persistenceUnitName);
        targetScanPackages = Arrays.asList(targetScanPackage);
    }

    /**
     * @param targetScanPackages packages to scan for repositories.
     */
    public ScanningJpaRepositoryModule(Iterable<String> targetScanPackages, String... persistenceUnitName) {
        super(persistenceUnitName);
        this.targetScanPackages = Lists.newArrayList(targetScanPackages);

    }
    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected void bindRepositories(RepositoryBinder binder) {
        Set<URL> urls = new HashSet<URL>();

        for (String targetScanPackage : targetScanPackages) {
            urls.addAll(ClasspathHelper.forPackage(targetScanPackage));
        }

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setUrls(urls);
        configurationBuilder.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());

        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<?>> repositoryClasses = new HashSet<Class<?>>();

        repositoryClasses.addAll(reflections.getTypesAnnotatedWith(Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(org.springframework.data.repository.Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(CrudRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(PagingAndSortingRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(JpaRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(BatchStoreJpaRepository.class));

        // Extraction of real Repository implementations (Classes)
        Collection<Class<?>> implementations = filter(repositoryClasses, new Predicate<Class<?>>() {
            public boolean apply(Class<?> input) {
                return !input.isInterface();
            }
        });

        for (final Class<?> repositoryClass : repositoryClasses) {
            // Autobind only for interfaces
            if (repositoryClass.isInterface()) {
                Collection<Class<?>> repoImplementations = filter(implementations, new Predicate<Class<?>>() {
                    public boolean apply(Class<?> input) {
                        return repositoryClass.isAssignableFrom(input);
                    }
                });

                Iterator<Class<?>> iterator = repoImplementations.iterator();
                Class<?> implementation = iterator.hasNext() ? iterator.next() : null;
                getLogger().info(String.format("Found repository: [%s]", repositoryClass.getName()));
                //TODO: resolving with annotation/top @Transactional
                bind(repositoryClass).toProvider(new JpaRepositoryProvider(repositoryClass, implementation, null));
            }
        }
    }
}