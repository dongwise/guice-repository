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

package com.google.code.guice.repository.testing.junit.autobind;

import com.google.code.guice.repository.testing.model.Account;
import com.google.code.guice.repository.testing.model.Customer;
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.model.UserData;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.repo.CustomerRepository;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.code.guice.repository.testing.runner.AutoBindRepoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AutoBindRepoTestRunner.class)
public class AutoBindRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private UserDataRepository userDataRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testUserRepository() {
        userRepository.save(new User("name", "surname", 42));
        assertEquals("Invalid repository size", 1, userRepository.count());
        userRepository.deleteOtherUsers();
        assertEquals(0, userRepository.count());
        userRepository.someCustomMethod(new User());
    }

    @Test
    public void testAccountRepository() {
        accountRepository.deleteAll();
        String uuid = UUID.randomUUID().toString();
        accountRepository.save(new Account(uuid, "lexx"));
        assertEquals("Invalid repository size", 1, accountRepository.count());
        assertNotNull("Unable to find account by name", accountRepository.findAccountByName("lexx"));
        assertNotNull("Unable to find account by UUID", accountRepository.findAccountByUuid(uuid));
        accountRepository.deleteAll();
        assertEquals("Invalid repository size", 0, accountRepository.count());
    }

    @Test
    public void testCustomerRepository() {
        customerRepository.save(new Customer("name", "surname"));
        customerRepository.sharedCustomMethod((long) 42);
        assertEquals("Invalid repository size", 1, customerRepository.count());
        customerRepository.deleteAll();
        assertEquals("Invalid repository size", 0, customerRepository.count());
    }

    @Test
    public void testUserDataRepository() {
        userDataRepository.save(new UserData());
        assertEquals("Invalid repository size", 1, userDataRepository.count());
        userDataRepository.deleteAll();
        assertEquals("Invalid repository size", 0, userDataRepository.count());
    }
}
