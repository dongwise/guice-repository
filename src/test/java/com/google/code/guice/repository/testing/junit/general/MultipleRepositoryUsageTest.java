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
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

public class MultipleRepositoryUsageTest extends RepoTestBase {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testRepositories() throws Exception {
        userRepository.save(new User("Alex", "Johns", 10));
        accountRepository.save(new Account(UUID.randomUUID().toString(), "1"));
        Assert.assertEquals("Invalid repository size", 1, userRepository.count());
        Assert.assertEquals("Invalid repository size", 1, accountRepository.count());
    }
}
