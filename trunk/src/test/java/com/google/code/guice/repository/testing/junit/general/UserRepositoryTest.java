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
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings({"MagicNumber"})
public class UserRepositoryTest extends RepoTestBase {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testRepo() throws Exception {
        userRepository.someCustomMethod(new User("one", "two", 42));

        userRepository.deleteInactiveUsers();
        userRepository.deleteOtherUsers();

        userRepository.deleteAll();
        assertEquals("Invalid repository size", 0, userRepository.count());

        userRepository.save(new User("john", "smith", 42));
        userRepository.save(new User("alex", "johns", 33));
        userRepository.save(new User("sam", "brown", 22));
        assertEquals("Invalid repository size", 3, userRepository.count());

        assertNotNull("User not found", userRepository.findUserByName("john"));

        Page<User> users = userRepository.findAll(new PageRequest(0, 100));
        assertEquals("Invalid requested page size", 3, users.getNumberOfElements());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.invokeAll(Arrays.asList(Executors.callable(new Runnable() {
            public void run() {
                try {
                    logger.info("Start concurrent thread");
                    UserRepository anotherRepo = userRepository;
                    logger.info("count");
                    assertEquals("Invalid repository size", 3, anotherRepo.count());
                    logger.info("save");
                    anotherRepo.save(new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 10));
                    assertEquals("Invalid repository size", 4, anotherRepo.count());
                    logger.info("Stored 4");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })));

        logger.info("After");
        assertEquals("Invalid repository size", 4, userRepository.count());
        userRepository.deleteAll();
        assertEquals("Invalid repository size", 0, userRepository.count());

        userRepository.someCustomMethod(new User("john", "smith", 42));
    }

    @Test
    @Transactional
    public void testLazyLoad(){
        User user = new User("lazy", "lazy", 1);
        String metadata = "lazy-metadata";
        user.setMetadata(Arrays.asList(metadata));
        userRepository.save(user);

        User lazyUser = userRepository.findUserByName("lazy");
        System.out.println("user");
        Collection<String> lMetadata = lazyUser.getMetadata();

        String loadedMetadata = lMetadata.iterator().next();
        assertEquals(metadata, loadedMetadata);
    }
}
