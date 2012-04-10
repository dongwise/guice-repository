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

package com.google.code.guice.repository.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.code.guice.repository.test.model.User;
import com.google.code.guice.repository.test.runner.ManualBindRepoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(ManualBindRepoTestRunner.class)
public class UserRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Provider<UserRepository> userRepositoryProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup() {
        userRepositoryProvider.get().deleteAll();
    }


    @Test
    public void testRepo() throws Exception {
        UserRepository userRepository = userRepositoryProvider.get();
        userRepository.someCustomMethod(new User("one", "two", 42));

        userRepository.deleteInactiveUsers();
        userRepository.deleteOtherUsers();

        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

        userRepository.save(new User("john", "smith", 42));
        userRepository.save(new User("alex", "johns", 33));
        userRepository.save(new User("sam", "brown", 22));
        assertEquals(3, userRepositoryProvider.get().count());

        Page<User> users = userRepository.findAll(new PageRequest(0, 100));
        assertEquals(3, users.getNumberOfElements());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.invokeAll(Arrays.asList(Executors.callable(new Runnable() {
            public void run() {
                try {
                    System.out.println("Start concurrent thread");
                    UserRepository anotherRepo = userRepositoryProvider.get();
                    System.out.println("count");
                    assertEquals(3, anotherRepo.count());
                    System.out.println("save");
                    anotherRepo.save(new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 10));
                    assertEquals(4, anotherRepo.count());
                    System.out.println("Stored 4");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })));

        System.out.println("After");
        assertEquals(4, userRepository.count());
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

        userRepository.someCustomMethod(new User("john", "smith", 42));
    }
}
