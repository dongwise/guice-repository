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

package com.google.code.guice.repository.testing.repo.custom;

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.UserRepositoryCustom;
import org.junit.Assert;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @PersistenceContext
    private EntityManager entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    @Transactional
    public void someCustomMethod(User user) {
        Assert.assertNotNull("User is null", user);
        Assert.assertNotNull("EntityManager is null", entityManager);
        System.out.println("user = " + user);
    }
}