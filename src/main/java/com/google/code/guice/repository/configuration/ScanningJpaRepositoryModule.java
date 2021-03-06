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
 * Guice module with Repository support and auto Repository scanning abilities. With this module there is no need to
 * manual binding of any Repositories - just specify target repository package :
 * <pre>
 *     install(new ScanningJpaRepositoryModule(
 *                     Arrays.asList(
 *                                 RepositoriesGroupBuilder.forPackage("com.mycorp.repo").
 *                                 withExclusionPattern(".*" + UserDataRepository.class.getSimpleName() + ".*").
 *                                 attachedTo("persistence-unit1").
 *                                 build();
 *                               ));
 * </pre>
 * <p/>
 * Also there is an option to combine auto-binding with manual binding. For this case just override {@link
 * #bindRepositories(RepositoryBinder)} with 'super' call:
 * <pre>
 *     install(new ScanningJpaRepositoryModule("com.mycorp.repo", "persistence-unit1") {
 *                     protected void bindRepositories(RepositoryBinder binder) {
 *                           super.bindRepositories(binder);
 *                           // manual bind specific repositories
 *                           binder.bind(MyRepository.class).withSelfDefinition();
 *                     }
 *               });
 * </pre>
 *
 * @author Alexey Krylov
 * @since 10.04.2012
 */
@SuppressWarnings("CollectionContainsUrl")
public class ScanningJpaRepositoryModule extends JpaRepositoryModule {

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    private Collection<RepositoriesGroup> repositoriesGroups;

	/*===========================================[ CONSTRUCTORS ]=================*/

    public ScanningJpaRepositoryModule(Collection<RepositoriesGroup> repositoriesGroups) {
        super(extractPersistenceUnitsNames(repositoriesGroups));
        this.repositoriesGroups = Lists.newArrayList(repositoriesGroups);
    }

    private static String[] extractPersistenceUnitsNames(Iterable<RepositoriesGroup> repositoriesGroups) {
        Collection<String> persistenceUnitsNames = new ArrayList<String>();
        for (RepositoriesGroup repositoriesGroup : repositoriesGroups) {
            persistenceUnitsNames.add(repositoriesGroup.getPersistenceUnitName());
        }

        return persistenceUnitsNames.toArray(new String[persistenceUnitsNames.size()]);
    }

    /**
     * @param targetScanPackage package to scan for repositories.
     */
    public ScanningJpaRepositoryModule(String targetScanPackage, String... persistenceUnitName) {
        super(persistenceUnitName);
        repositoriesGroups = new ArrayList<RepositoriesGroup>();
        repositoriesGroups.add(new RepositoriesGroup(targetScanPackage, persistenceUnitsNames[0]));
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void bindRepositories(RepositoryBinder binder) {
        for (RepositoriesGroup group : repositoriesGroups) {
            String persistenceUnitName = group.getPersistenceUnitName();

            Set<URL> urls = new HashSet<URL>();
            Collection<String> packagesToScan = group.getRepositoriesPackages();
            getLogger().info(String.format("Scanning %s for [%s]", packagesToScan, persistenceUnitName));

            for (String packageName : packagesToScan) {
                urls.addAll(ClasspathHelper.forPackage(packageName));
            }

            Collection<Class<?>> repositoryClasses = findRepositories(urls);

            // Extraction of real Repository implementations (Classes)
            Collection<Class<?>> implementations = filter(repositoryClasses, new Predicate<Class<?>>() {
                @Override
                public boolean apply(Class<?> input) {
                    return !input.isInterface();
                }
            });

            for (final Class<?> repositoryClass : repositoryClasses) {
                // Autobind only for interfaces matched with group filters/patterns
                if (repositoryClass.isInterface() && group.matches(repositoryClass)) {
                    // Custom implementations
                    Collection<Class<?>> repoImplementations = filter(implementations, new Predicate<Class<?>>() {
                        @Override
                        public boolean apply(Class<?> input) {
                            return repositoryClass.isAssignableFrom(input);
                        }
                    });

                    Iterator<Class<?>> iterator = repoImplementations.iterator();
                    Class<?> implementation = iterator.hasNext() ? iterator.next() : null;
                    getLogger().info(String.format("Found repository: [%s]", repositoryClass.getName()));
                    binder.bind(repositoryClass).withCustomImplementation(implementation).to(persistenceUnitName);
                }
            }
        }
    }

    protected Set<Class<?>> findRepositories(Set<URL> scanUrls) {
        Reflections reflections = createReflections(scanUrls);
        Set<Class<?>> repositoryClasses = new HashSet<Class<?>>();
        repositoryClasses.addAll(reflections.getTypesAnnotatedWith(Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(org.springframework.data.repository.Repository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(CrudRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(PagingAndSortingRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(JpaRepository.class));
        repositoryClasses.addAll(reflections.getSubTypesOf(BatchStoreJpaRepository.class));
        return repositoryClasses;
    }

    protected Reflections createReflections(Collection<URL> urls) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setUrls(urls);
        configurationBuilder.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
        return new Reflections(configurationBuilder);
    }
}