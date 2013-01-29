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


import com.google.code.guice.repository.BatchStoreJpaRepository;
import com.google.code.guice.repository.EntityManagerProvider;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves custom repository implementations.
 * <pre>
 * See full specifics <a href="http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#repositories.single-repository-behaviour">here</a>
 * </pre>
 *
 * @author Alexey Krylov
 * @since 10.04.2012
 */
public class CustomRepositoryImplementationResolver {

	/*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(CustomRepositoryImplementationResolver.class);
    private static final String SPRING_DATA_PACKAGE = "org.springframework.data";

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    private Injector injector;
    private Set<Class> exclusions;

	/*===========================================[ CONSTRUCTORS ]=================*/

    @Inject
    protected void init(Injector injector) {
        this.injector = injector;
        exclusions = new HashSet<Class>();
        addExclusions(exclusions);
    }

	/*===========================================[ CLASS METHODS ]================*/

    protected void addExclusions(Collection<Class> exclusions) {
        exclusions.addAll(Arrays.asList(BatchStoreJpaRepository.class, EntityManagerProvider.class));
    }

    public Class resolve(Class<? extends Repository> repositoryClass) {
        Assert.notNull(repositoryClass);

        Class customRepository = null;
        /**
         * Detect only custom repository/enhancements interfaces - skip all from Spring and guice-repository project
         */
        Collection<Class<?>> superTypes = ReflectionUtils.getAllSuperTypes(repositoryClass, new Predicate<Class>() {
            @Override
            public boolean apply(Class input) {
                return isValidCustomInterface(input);
            }
        });

        /**
         * Searching for custom repository/enhancement implementation
         */
        if (!superTypes.isEmpty()) {
            Class customRepositoryClass = superTypes.iterator().next();
            Reflections reflections = new Reflections(customRepositoryClass.getPackage().getName());
            Iterable<Class> subTypesOf = reflections.getSubTypesOf(customRepositoryClass);
            for (Class aClass : subTypesOf) {
                if (!aClass.isInterface()) {
                    customRepository = aClass;
                    logger.info(String.format("Found custom repository implementation: [%s] -> [%s]", repositoryClass.getName(), customRepository.getName()));
                    break;
                }
            }
        }

        return customRepository;
    }

    protected boolean isValidCustomInterface(Class aClass) {
        return !exclusions.contains(aClass) &&
                !aClass.getName().startsWith(SPRING_DATA_PACKAGE) &&
                injector.getExistingBinding(Key.get((Class<Object>) aClass)) == null;
    }
}
