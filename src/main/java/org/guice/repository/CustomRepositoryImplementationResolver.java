/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 04.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository;


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
 *
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

    public Class resolve(Class<? extends Repository> repositoryClass, Class domainClass) {
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
