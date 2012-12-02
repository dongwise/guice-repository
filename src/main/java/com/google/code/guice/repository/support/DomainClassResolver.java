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

package com.google.code.guice.repository.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Resolves Domain Class from class type parameter. For example: for repository like this
 * <pre>
 *      com.mycorp.repo.MyRepository&lt;com.mycorp.model.User&gt;
 *  </pre>
 * domain class will be resolved to <code>com.mycorp.model.User</code>
 *
 * @author Alexey Krylov
 * @version 1.0.0
 * @since 10.04.2012
 */
public class DomainClassResolver {

    /*===========================================[ CLASS METHODS ]==============*/
    //TODO: replace with type util
    public Class resolve(Class aClass) {
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