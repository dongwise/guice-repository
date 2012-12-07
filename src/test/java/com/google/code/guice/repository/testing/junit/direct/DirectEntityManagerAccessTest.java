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

package com.google.code.guice.repository.testing.junit.direct;

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.model.UserData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * DirectEntityManagerAccessTest - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 06.12.12
 */
@RunWith(DirectEntityManagerTestRunner.class)
public class DirectEntityManagerAccessTest {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @PersistenceContext
    private EntityManager entityManager;

    @PersistenceContext(unitName = "test-h2-secondary")
    private EntityManager secondaryEntityManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Test
    @Transactional
    public void testPrimaryEntityManager() {
        entityManager.persist(new User());
        List resultList = entityManager.createQuery("from User").getResultList();
        Assert.assertEquals("No users found", 1, resultList.size());
    }

    @Test
    @Transactional("test-h2-secondary")
    public void testSecondaryEntityManager() {
        secondaryEntityManager.persist(new UserData());
        List resultList = secondaryEntityManager.createQuery("from UserData").getResultList();
        Assert.assertEquals("No users data found", 1, resultList.size());
    }
}