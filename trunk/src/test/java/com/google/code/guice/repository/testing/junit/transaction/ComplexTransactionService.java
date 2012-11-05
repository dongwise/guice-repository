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

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Provider;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@SuppressWarnings({"MagicNumber"})
public class ComplexTransactionService {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(ComplexTransactionService.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private Provider<EntityManager> entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Transactional(rollbackFor = Exception.class)
    public void performFirstComplexTransaction() throws Exception {
        logger.info("EM for performComplexTransaction: " + entityManager.get());
        logger.info("DeleteAll");
        userRepository.deleteAll();
        logger.info("Save1");
        userRepository.save(new User("John", "Smith", 42));
        logger.info("Save2");
        userRepository.save(new User("Alex", "Johns", 22));
        logger.info("Count1");

        // partially committed
        Assert.assertEquals("Invalid repository size", 2, userRepository.count());
        throw new Exception("First rollback");
    }

    @Transactional(rollbackFor = Exception.class)
    public void performSecondComplexTransaction() throws Exception {
        logger.info("Checking size");
        Assert.assertEquals("Invalid repository size", 0, userRepository.count());
        logger.info("EM for performSecondComplexTransaction: " + entityManager.get());
        logger.info("Save3");
        userRepository.save(new User("1", "1", 1));
        logger.info("Count2");
        Assert.assertEquals("Invalid repository size", 1, userRepository.count());
        throw new Exception("Second rollback");
    }
}