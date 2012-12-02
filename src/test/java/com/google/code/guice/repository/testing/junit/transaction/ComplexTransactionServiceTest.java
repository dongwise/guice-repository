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

package com.google.code.guice.repository.testing.junit.transaction;

import com.google.code.guice.repository.testing.junit.RepoTestBase;
import com.google.code.guice.repository.testing.model.Account;
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionTimedOutException;

import java.util.UUID;

public class ComplexTransactionServiceTest extends RepoTestBase {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private ComplexTransactionService complexTransactionService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testCompositeTransactions() throws Exception {
        try {
            complexTransactionService.performFirstComplexTransaction();
        } catch (Exception e) {
            logger.error("Error", e);
        }
        logger.info("Main: checking size");
        long count = userRepository.count();
        Assert.assertEquals("Invalid repository size", 0, count);

        try {
            complexTransactionService.performSecondComplexTransaction();
        } catch (Exception e) {
            logger.error("Error", e);
        }

        // no users should be added because we throw a flow-breaker exception and rollback the transaction
        Assert.assertEquals("Invalid repository size", 0, userRepository.count());

        userRepository.save(new User("1", "1", 1));
        userRepository.save(new User("2", "2", 2));
        accountRepository.save(new Account(UUID.randomUUID().toString(), "1"));
        accountRepository.save(new Account(UUID.randomUUID().toString(), "2"));
        Assert.assertEquals("Invalid repository size", 2, userRepository.count());
        Assert.assertEquals("Invalid repository size", 2, accountRepository.count());

        try {
            complexTransactionService.performThirdComplexTransaction();
        } catch (Exception e) {
            logger.error("Error", e);
        }
        Assert.assertEquals("Invalid repository size", 2, userRepository.count());
        Assert.assertEquals("Invalid repository size", 2, accountRepository.count());
    }

    @Test
    public void testTimeoutedTransaction() throws Exception {
        Exception lastException = null;

        try {
            complexTransactionService.testTimeoutedTransaction();
        } catch (Exception e) {
            lastException = e;
        }
        Assert.assertNotNull("No exception has been received", lastException);
        Assert.assertTrue("Received exception is not TransactionTimeOutException: " + lastException.getClass(), lastException instanceof TransactionTimedOutException);
        Assert.assertEquals("Invalid repository size", 0, userRepository.count());
    }

}