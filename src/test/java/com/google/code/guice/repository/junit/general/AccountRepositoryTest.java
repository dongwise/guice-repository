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

package com.google.code.guice.repository.junit.general;

import com.google.code.guice.repository.model.Account;
import com.google.code.guice.repository.repo.AccountRepository;
import com.google.code.guice.repository.junit.RepoTestBase;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Simple Repo-interface test
 *
 * @author Alexey Krylov
 */
public class AccountRepositoryTest extends RepoTestBase {

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

        try {
            accountRepository.save(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("Invalid repository size", count, accountRepository.count());
        assertNotNull("Account not found by name", accountRepository.findAccountByName(String.valueOf(1)));
        assertNotNull("Account not found by UUID", accountRepository.findAccountByUuid(accounts.get(0).getUuid()));
    }
}