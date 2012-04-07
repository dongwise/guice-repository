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

package org.guice.repository.test;

import com.google.inject.Injector;
import org.guice.repository.test.model.User;
import org.guice.repository.test.runner.ManualBindRepoTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@RunWith(ManualBindRepoTestRunner.class)
public class MultiThreadedRepositoryTest {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final int MAX_CONCURRENT_THREADS = 100;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();

        Runnable repoExploiter = new Runnable() {
            public void run() {
                UserRepository instance = injector.getInstance(UserRepository.class);
                instance.save(new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 1));
            }
        };
        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            callables.add(Executors.callable(repoExploiter));
        }

        Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS).invokeAll(callables);

        Assert.assertEquals("Invalid entities count", MAX_CONCURRENT_THREADS, injector.getInstance(UserRepository.class).count());
    }
}
