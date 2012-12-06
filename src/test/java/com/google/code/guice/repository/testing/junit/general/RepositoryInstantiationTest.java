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

import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.code.guice.repository.testing.junit.RepoTestBase;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RepositoryInstantiationTest extends RepoTestBase {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final int TEST_DURATION_SECONDS = 5;

    /*===========================================[ CLASS METHODS ]==============*/

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "JUnitTestMethodWithNoAssertions"})
    @Test
    public void testInstantiationSpeed() throws Exception {
        logger.info("Warming up...");
        doTest(5);
        logger.info("Testing...");
        Long instanceCount = doTest(TEST_DURATION_SECONDS);
        logger.info(String.format("Speed: [%d] instances/sec", instanceCount / TEST_DURATION_SECONDS));
    }

    private Long doTest(int testTime) throws InterruptedException {
        final CountDownLatch testTimeLatch = new CountDownLatch(testTime);
        final Set<Integer> hashCodes = new HashSet<Integer>();
        final AtomicLong instanceCounter = new AtomicLong();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                testTimeLatch.countDown();
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));


        final AtomicBoolean working = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (working.get()) {
                    UserRepository instance = injector.getInstance(UserRepository.class);
                    // avoid code block removing by HotSpot optimizations
                    int hashCode = instance.hashCode();
                    hashCodes.add(hashCode);
                    instanceCounter.incrementAndGet();
                }
            }
        });

        // real test period
        testTimeLatch.await(testTime, TimeUnit.SECONDS);
        logger.info("Hashcodes size: " + hashCodes.size());
        return instanceCounter.get();
    }
}