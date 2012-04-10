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

package com.google.code.guice.repository;

import com.google.code.guice.repository.model.Account;
import com.google.code.guice.repository.repo.AccountRepository;
import com.google.code.guice.repository.runner.ManualBindRepoTestRunner;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Simple Repo-interface test
 *
 * @author Alexey Krylov AKA lexx
 */
@RunWith(ManualBindRepoTestRunner.class)
public class AccountRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private AccountRepository accountRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup() {
        accountRepository.deleteAll();
    }

    @Test
    public void testRepo() throws Exception {
        List<Account> accounts = new LinkedList<Account>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            accounts.add(new Account(UUID.randomUUID().toString(), String.valueOf(i)));
        }

        accountRepository.save(accounts);
        assertEquals(count, accountRepository.count());
        assertNotNull(accountRepository.findAccountByName(String.valueOf(1)));
        assertNotNull(accountRepository.findAccountByUuid(accounts.get(0).getUuid()));
    }
}