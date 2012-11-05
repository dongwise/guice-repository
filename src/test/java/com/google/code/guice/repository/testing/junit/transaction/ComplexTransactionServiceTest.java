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
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Alexey Krylov AKA lexx
 */
public class ComplexTransactionServiceTest extends RepoTestBase{

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private ComplexTransactionService complexTransactionService;

    @Inject
    private UserRepository repository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testCompositeTransactions() throws Exception {
        try {
            complexTransactionService.performFirstComplexTransaction();
        } catch (Exception e) {
            logger.error("Error", e);
        }
        logger.info("Main: checking size");
        long count = repository.count();
        Assert.assertEquals("Invalid repository size", 0, count);

        try {
            complexTransactionService.performSecondComplexTransaction();
        } catch (Exception e) {
            logger.error("Error", e);
        }

        count = repository.count();
        // no users should be added because we throw a flow-breaker exception and rollback the transaction
        Assert.assertEquals("Invalid repository size", 0, count);
    }
}