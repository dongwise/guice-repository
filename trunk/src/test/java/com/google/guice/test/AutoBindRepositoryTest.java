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

package com.google.guice.test;

import com.google.guice.test.runner.AutoBindRepoTestRunner;
import com.google.guice.test.model.Account;
import com.google.guice.test.model.Customer;
import com.google.guice.test.model.User;
import org.junit.Before;
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

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup(){
        userRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    public void testUserRepository(){
        userRepository.save(new User("name", "surname", 42));
        assertEquals(1, userRepository.count());
        userRepository.deleteOtherUsers();
        assertEquals(0, userRepository.count());
    }

    @Test
    public void testAccountRepository(){
        accountRepository.deleteAll();
        String uuid = UUID.randomUUID().toString();
        accountRepository.save(new Account(uuid, "lexx"));
        assertEquals(1, accountRepository.count());
        assertNotNull(accountRepository.findAccountByName("lexx"));
        assertNotNull(accountRepository.findAccountByUuid(uuid));
        accountRepository.deleteAll();
        assertEquals(0, accountRepository.count());
    }

    @Test
    public void testCustomerRepository(){
        customerRepository.save(new Customer("name", "surname"));
        customerRepository.sharedCustomMethod(new Long(42));
        assertEquals(1, customerRepository.count());
        customerRepository.deleteAll();
        assertEquals(0, customerRepository.count());
    }
}
