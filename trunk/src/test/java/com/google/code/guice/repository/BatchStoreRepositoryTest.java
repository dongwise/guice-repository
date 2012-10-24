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
import com.google.inject.Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(ManualBindRepoTestRunner.class)
public class BatchStoreRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Provider<AccountRepository> accountRepositoryProvider;
    private Timer timer;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void before() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(String.format("Current count: [%d], free memory: [%d] mb", accountRepositoryProvider.get().count(),
                        Runtime.getRuntime().freeMemory() / (1024 * 1024)));
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
        accountRepositoryProvider.get().deleteAll();
    }

    @After
    public void after() {
        AccountRepository accountRepository = accountRepositoryProvider.get();
        accountRepository.deleteAll();
        assertEquals(0, accountRepository.count());
        timer.cancel();
    }

    @SuppressWarnings({"CallToSystemGC"})
    @Test
    public void testDefaultBatchSave() throws Exception {
        long initialMemory = Runtime.getRuntime().freeMemory();
        long percent = initialMemory / 100;

        int batchSize = 1000;
        int iterationsCount = 100;

        AccountRepository accountRepository = accountRepositoryProvider.get();
        for (int i = 0; i < iterationsCount; i++) {
            List<Account> accounts = generateBatch(batchSize);
            accountRepository.saveInBatch(accounts);
        }

        assertEquals("Invalid stored entities count", iterationsCount * batchSize, accountRepository.count());
        Runtime.getRuntime().gc();
        long free = Runtime.getRuntime().freeMemory();
        // Memory usage no more than 20% of initial
        assertTrue("Memory leak detected", free >= initialMemory - 20 * percent);
    }

    @Test
    public void testPartialBatchSave() throws Exception {
        AccountRepository accountRepository = accountRepositoryProvider.get();

        int totalSize = 10000;

        List<Account> accounts = generateBatch(totalSize);
        accountRepository.saveInBatch(accounts, 100);

        assertEquals("Invalid stored entities count", totalSize, accountRepository.count());
    }

    private List<Account> generateBatch(int batchSize) {
        List<Account> accounts = new LinkedList<Account>();
        for (int i = 0; i < batchSize; i++) {
            accounts.add(new Account(UUID.randomUUID().toString(), String.valueOf(i)));
        }
        return accounts;
    }
}