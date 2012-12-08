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

package com.google.code.guice.repository.testing.junit.transaction;

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@SuppressWarnings({"MagicNumber"})
public class BasicTransactionService {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(BasicTransactionService.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Transactional(rollbackFor = Exception.class)
    public void generateUsers(int usersCount) throws Exception {
        for (int i = 0; i < usersCount; i++) {
            userRepository.save(new User("Name" + i, "Surname" + i, i));
            logger.info("Generated: " + i);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUsersOlderThan(int age) throws Exception {
        userRepository.getEntityManager().createQuery("delete from User where age > " + age).executeUpdate();
    }
}