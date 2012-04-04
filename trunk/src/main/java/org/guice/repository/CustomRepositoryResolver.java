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
import org.springframework.data.repository.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CustomRepositoryResolver {
    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Map<Class<? extends Repository>, Object> customRepositories;
    private Injector injector;

    /*===========================================[ CONSTRUCTORS ]===============*/

    @Inject
    public void init(Injector injector) {
        this.injector = injector;
        customRepositories = new ConcurrentHashMap<Class<? extends Repository>, Object>();
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public Object resolve(Class<? extends Repository> repositoryClass, Class domainClass) {
        Assert.notNull(repositoryClass);

        Object customRepository = customRepositories.get(repositoryClass);
        if (customRepository == null) {
            //TODO TRY TO RESOLVE
            Collection<? extends Class<?>> superTypes = ReflectionUtils.getAllSuperTypes(repositoryClass);
            for (Class<?> superType : superTypes) {

            }

            Collection<? extends Class<?>> unbindedSupertypes = Collections2.filter(superTypes, new Predicate<Object>() {
                public boolean apply(Object input) {
                    return input instanceof Class
                            && !((Class) input).getName().startsWith("org.springframework.data")
                            && injector.getExistingBinding(Key.get((Class<Object>) input)) == null;
                }
            });

            for (Class unbindedSupertype : unbindedSupertypes) {
                Reflections reflections = new Reflections(unbindedSupertype.getPackage().getName());

                Set<Class> subTypesOf = reflections.getSubTypesOf(unbindedSupertype);
                for (Class aClass : subTypesOf) {

                }
            }
        }

//        injector.getInstance();
        //TODO: inject members
        Class<?> aClass = SimpleBatchStoreJpaRepository.class;
        try {
            aClass = Class.forName("org.guice.repository.test.UserRepositoryCustomImpl");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return null;
//            return aClass.newInstance();
        } catch (Exception e) {
            return null;
        }
//        return customRepository;
    }
}
