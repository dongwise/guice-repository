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

package com.googlecode.guicerepository;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

/**
 * Resolves custom repository implementations.
 * <pre>
 * See full specifics <a href="http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#repositories.single-repository-behaviour">here</a>
 * </pre>
 *
 * @author Alexey Krylov AKA lexx
 */
class CustomRepositoryImplementationResolver {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(CustomRepositoryImplementationResolver.class);
    private static final String SPRING_DATA_PACKAGE = "org.springframework.data";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Injector injector;

    /*===========================================[ CONSTRUCTORS ]===============*/

    @Inject
    public void init(Injector injector) {
        this.injector = injector;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    Class resolve(Class<? extends Repository> repositoryClass) {
        Assert.notNull(repositoryClass);

        Class customRepository = null;
        Collection<? extends Class<?>> superTypes = ReflectionUtils.getAllSuperTypes(repositoryClass);

        /**
         * Detect only custom repository/enhancements interfaces
         */
        Collection<? extends Class<?>> unbindedSupertypes = Collections2.filter(superTypes, new Predicate<Object>() {
            public boolean apply(Object input) {
                if (input instanceof Class) {
                    Class aClass = ((Class) input);
                    return !aClass.getName().startsWith(SPRING_DATA_PACKAGE) &&
                            !aClass.getPackage().equals(getClass().getPackage()) &&
                            injector.getExistingBinding(Key.get((Class<Object>) aClass)) == null;
                }

                return false;
            }
        });

        /**
         * Searching for custom repository/enhancement implementation
         */
        if (!unbindedSupertypes.isEmpty()) {
            Class customRepositoryClass = unbindedSupertypes.iterator().next();
            Reflections reflections = new Reflections(customRepositoryClass.getPackage().getName());

            Set<Class> subTypesOf = reflections.getSubTypesOf(customRepositoryClass);
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
}
