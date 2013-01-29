/*
 * Copyright (C) 2013 the original author or authors.
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


package com.google.code.guice.repository.testing.junit.general.threading;

import com.google.code.guice.repository.testing.junit.RepoTestBase;
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.repo.UserRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MultiEditorsRepositoryTest - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 30.01.13
 */
public class MultiEditorsRepositoryTest extends RepoTestBase{

	/*===========================================[ CLASS METHODS ]================*/

    @Test
    public void multiThreadedSingleEntityModificationTest() throws Exception {

        UserRepository userRepository = injector.getInstance(UserRepository.class);
        User t = new User();
        t.setAge(42);

        userRepository.save(t);

        final CountDownLatch secondCheckShouldStart = new CountDownLatch(1);
        final CountDownLatch firstCheckShouldStart = new CountDownLatch(1);
        final CountDownLatch testDone = new CountDownLatch(1);

        final Throwable[] fail = new Throwable[1];
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    UserRepository userRepository = injector.getInstance(UserRepository.class);
                    List<User> list = userRepository.findAll();
                    Assert.assertEquals(42, list.get(0).getAge());
                    secondCheckShouldStart.countDown();

                    firstCheckShouldStart.await();
                    UserRepository userRepository1 = injector.getInstance(UserRepository.class);
                    List<User> list1 = userRepository1.findAll();
                    User user = list1.get(0);
                    Assert.assertEquals(10, user.getAge());
                } catch (Throwable e) {
                    fail[0] = e;
                } finally {
                    testDone.countDown();
                }
            }

        });

        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        executorService2.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    secondCheckShouldStart.await();

                    UserRepository userRepository = injector.getInstance(UserRepository.class);
                    List<User> list = userRepository.findAll();
                    User user = list.get(0);
                    user.setAge(10);
                    userRepository.save(user);

                    UserRepository userRepository1 = injector.getInstance(UserRepository.class);
                    List<User> templates = userRepository1.findAll();
                    Assert.assertEquals(10, templates.get(0).getAge());

                    firstCheckShouldStart.countDown();
                } catch (Throwable e) {
                    fail[0] = e;
                }
            }

        });

        testDone.await();
        Throwable exception = fail[0];
        if (exception != null) {
            logger.error("Error", exception);
            Assert.fail(exception.getMessage());
        }
    }
}
