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

import com.google.inject.Injector;
import com.google.guice.test.runner.ManualBindRepoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(ManualBindRepoTestRunner.class)
public class MultiThreadedRepositoryInstantiationTest {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final int MAX_CONCURRENT_THREADS = 1000;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;
    private AtomicLong instanceCounter;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup() {
        injector.getInstance(UserRepository.class).deleteAll();
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        instanceCounter = new AtomicLong();
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();

        Runnable repoCreator = new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    UserRepository instance = injector.getInstance(UserRepository.class);
                    instanceCounter.incrementAndGet();
                }
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            executorService.execute(repoCreator);
        }

        System.out.println("Awaiting for generation");

        int testTimeSeconds = 10;
        TimeUnit.SECONDS.sleep(testTimeSeconds);
        executorService.shutdown();
        System.out.println(String.format("Speed: [%d] instances/sec", instanceCounter.get()/testTimeSeconds));
    }
}
