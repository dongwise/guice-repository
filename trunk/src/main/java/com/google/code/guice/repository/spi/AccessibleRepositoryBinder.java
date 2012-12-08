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

import com.google.code.guice.repository.configuration.RepositoryBinder;
import com.google.code.guice.repository.configuration.RepositoryBinding;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * DefaultRepositoryBinder - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 07.12.12
 */
public class AccessibleRepositoryBinder implements RepositoryBinder {
    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private Collection<AccessibleRepositoryBinding> bindings;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public AccessibleRepositoryBinder() {
        bindings = new ArrayList<AccessibleRepositoryBinding>();
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public RepositoryBinding bind(Class repositoryClass) {
        Assert.notNull(repositoryClass);
        AccessibleRepositoryBinding binding = new AccessibleRepositoryBinding(repositoryClass);
        bindings.add(binding);
        return binding;
    }

    public Collection<AccessibleRepositoryBinding> getBindings() {
        return Collections.unmodifiableCollection(bindings);
    }
}
