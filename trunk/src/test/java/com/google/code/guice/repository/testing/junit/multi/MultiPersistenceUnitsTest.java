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

package com.google.code.guice.repository.testing.junit.multi;

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.model.UserData;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * MultiDatasourceTest -
 *
 * @author Alexey Krylov (AleX)
 * @since 02.12.12
 */
@RunWith(MultiPersistenceUnitsTestRunner.class)
public class MultiPersistenceUnitsTest {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(MultiPersistenceUnitsTest.class);

    /*===========================================[ INSTANCE VARIABLES ]==========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserDataRepository userDataRepository;

    @Inject
    private MultiPersistenceUnitsService multiPersistenceUnitsService;

    /*===========================================[ CLASS METHODS ]===============*/

    @Before
    public void before() {
        userRepository.deleteAll();
        userDataRepository.deleteAll();
    }

    @Test
    public void multiPersistenceUnitsServiceTests() {
        int count = 100;
        multiPersistenceUnitsService.generateUsers(count);
        multiPersistenceUnitsService.generateUserData(count);
        Assert.assertEquals("Invalid generated users count", count, userRepository.count());
        Assert.assertEquals("Invalid generated user data count", count, userDataRepository.count());
        try {
            multiPersistenceUnitsService.generateUserDataWithRollback(100);
        } catch (Exception e) {
            logger.error("Error", e);
        }
        Assert.assertEquals("Invalid generated user data count after rollback", count, userDataRepository.count());
        userRepository.deleteAll();
        userDataRepository.deleteAll();
    }

    @Test
    public void testSave() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            userDataRepository.save(new UserData(String.valueOf(UUID.randomUUID())));
        }

        Assert.assertEquals("Invalid generated user data count", count, userDataRepository.count());
    }

    @Test
    public void complexTest() {
        // TODO: http://blog.springsource.org/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/#comment-198835
        //TODO: 1. создать разные экземпляры TransactionManager
        userRepository.save(new User("user", "surname", 1));
        userDataRepository.save(new UserData(UUID.randomUUID().toString()));
        Assert.assertEquals("No User saved", 1, userRepository.count());
        Assert.assertEquals("No UserData saved", 1, userDataRepository.count());
    }
}