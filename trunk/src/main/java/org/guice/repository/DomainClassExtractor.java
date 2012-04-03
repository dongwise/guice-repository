/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 04.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class DomainClassExtractor {

    /*===========================================[ CONSTRUCTORS ]===============*/

    private DomainClassExtractor() {
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public static Class extact(Class aClass) {
        Type parent = aClass.getGenericSuperclass();
        if (parent == null) {
            parent = aClass.getGenericInterfaces()[0];
        }

        if (!(parent instanceof ParameterizedType)) {
            Class parentClass = aClass.getSuperclass();
            parent = parentClass.getGenericSuperclass();
        }

        return (Class) ((ParameterizedType) parent).getActualTypeArguments()[0];
    }
}