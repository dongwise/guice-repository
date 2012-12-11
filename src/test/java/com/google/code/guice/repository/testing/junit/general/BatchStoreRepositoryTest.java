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

package com.google.code.guice.repository.testing.junit.general;

import com.google.code.guice.repository.testing.junit.RepoTestBase;
import com.google.code.guice.repository.testing.model.Account;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("MagicNumber")
public class BatchStoreRepositoryTest extends RepoTestBase {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private AccountRepository accountRepository;
    private Timer timer;

    /*===========================================[ CLASS METHODS ]================*/

    @Before
    public void before() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    long free = Runtime.getRuntime().freeMemory() / (1024 * 1024);
                    logger.info(String.format("Current count: [%d], free memory: [%d] mb", accountRepository.count(),
                            free));
                } catch (Throwable e) {
                    logger.error("Error", e);
                }
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    }

    @After
    public void after() {
        accountRepository.deleteAll();
        assertEquals("Invalid repository size", 0, accountRepository.count());
        timer.cancel();
    }

    @SuppressWarnings("CallToSystemGC")
    @Test
    public void testDefaultBatchSave() throws Exception {
        long initialMemory = Runtime.getRuntime().freeMemory();
        long percent = initialMemory / 100;

        int batchSize = 1000;
        int iterationsCount = 50;

        for (int i = 0; i < iterationsCount; i++) {
            List<Account> accounts = generateBatch(batchSize);
            accountRepository.saveInBatch(accounts);
        }

        assertEquals("Invalid stored entities count", iterationsCount * batchSize, accountRepository.count());
        timer.cancel();
        Runtime.getRuntime().gc();
        long free = Runtime.getRuntime().freeMemory();
        // Memory usage no more than 20% of initial
        assertTrue("Memory leak detected", free >= initialMemory - 20 * percent);
    }

    @Test
    public void testPartialBatchSave() throws Exception {
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