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

package com.google.code.guice.repository.testing.junit.general;

import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.junit.RepoTestBase;
import com.google.inject.Inject;
import org.junit.Test;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import static org.junit.Assert.assertTrue;

/**
 * Compares EntityManager equality between injected one and repository's internal.
 *
 * @author Alexey Krylov
 */
public class EntityManagerRepoEqualityTest extends RepoTestBase {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Provider<EntityManager> entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testRepo() throws Exception {
        assertTrue("EntityManager's is not equal", accountRepository.getEntityManager().equals(entityManager.get()));
    }
}