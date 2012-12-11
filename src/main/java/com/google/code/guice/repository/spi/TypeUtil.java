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

import net.jcip.annotations.ThreadSafe;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility for resolving actual classes of type parameters.
 *
 * @author Alexey Krylov
 * @see #getFirstTypeParameterClass(Class)
 * @see #getTypeParameterClass(Class, int)
 * @see #getTypeParameterClass(Class, Class)
 * @since 22.11.12
 */
@ThreadSafe
public class TypeUtil {

    /*===========================================[ CONSTRUCTORS ]=================*/

    private TypeUtil() {
    }

    /*===========================================[ CLASS METHODS ]================*/

    /**
     * Finds and returns class of first parametrization parameter.
     *
     * @param aClass generic class
     *
     * @return parameter class or {@code null} if parameter can't be found
     *
     * @throws IllegalArgumentException if specified {@code aClass} is null
     */
    public static Class getFirstTypeParameterClass(Class aClass) {
        Assert.notNull(aClass);
        return getTypeParameterClass(aClass, 0);
    }

    /**
     * Finds and returns class of N parametrization parameter.
     *
     * @param aClass         generic class
     * @param parameterIndex parameter index
     *
     * @return parameter class or {@code null} if parameter can't be found
     *
     * @throws IllegalArgumentException if specified {@code aClass} is null or {@code parameterIndex} &lt; 0
     */
    public static Class getTypeParameterClass(Class aClass, int parameterIndex) {
        Assert.notNull(aClass);
        Assert.isTrue(parameterIndex >= 0);

        List<Type> types = new ArrayList<Type>();

        // check interfaces
        getGenericInterfacesActualTypes(types, aClass);

        Class result = findAppropriateType(types, parameterIndex);
        if (result == null) {
            types.clear();
            // check superclasses
            getGenericSuperclassActualTypes(types, aClass);
        }
        return findAppropriateType(types, parameterIndex);
    }

    private static Class findAppropriateType(List<Type> types, int parameterIndex) {
        for (int i = 0; i < types.size(); i++) {
            if (i == parameterIndex) {
                Type type = types.get(i);
                if (type instanceof Class) {
                    return (Class) type;
                }
            }
        }
        return null;
    }

    /**
     * Finds and returns class of specified generic parametrization class.
     *
     * @param aClass                generic class
     * @param genericParameterClass generic parametrization class
     *
     * @return parameter class or {@code null} if parameter can't be found
     *
     * @throws IllegalArgumentException if specified {@code aClass}  or {@code genericParameterClass} is null
     */
    public static <T> Class<T> getTypeParameterClass(Class aClass, Class<T> genericParameterClass) {
        Assert.noNullElements(new Object[]{aClass, genericParameterClass});

        List<Type> types = new ArrayList<Type>();

        // check interfaces
        getGenericInterfacesActualTypes(types, aClass);

        Class result = findAppropriateType(types, genericParameterClass);
        if (result == null) {
            types.clear();
            // check superclasses
            getGenericSuperclassActualTypes(types, aClass);
        }
        return findAppropriateType(types, genericParameterClass);
    }

    private static <T> Class<T> findAppropriateType(Collection<Type> types, Class<T> genericParameterClass) {
        for (Type type : types) {
            if (type instanceof Class && genericParameterClass.isAssignableFrom((Class<?>) type)) {
                return (Class) type;
            }
        }
        return null;
    }

    public static void getGenericInterfacesActualTypes(Collection<Type> types, Class aClass) {
        if (aClass != null && types != null) {
            Type[] interfaces = aClass.getGenericInterfaces();
            for (Type anInterface : interfaces) {
                if (anInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) anInterface;
                    Type[] actualTypes = parameterizedType.getActualTypeArguments();
                    types.addAll(Arrays.asList(actualTypes));
                } else if (anInterface instanceof Class) {
                    Class typeClass = (Class) anInterface;
                    getGenericInterfacesActualTypes(types, typeClass);
                }
            }
        }
    }

    public static void getGenericSuperclassActualTypes(Collection<Type> types, Class aClass) {
        if (aClass != null && types != null) {
            Type superclass = aClass.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) superclass;
                Type[] interfaces = parameterizedType.getActualTypeArguments();
                types.addAll(Arrays.asList(interfaces));
            } else if (superclass instanceof Class) {
                Class sClass = (Class) superclass;
                getGenericInterfacesActualTypes(types, sClass);
                getGenericSuperclassActualTypes(types, aClass.getSuperclass());
            }
        }
    }
}