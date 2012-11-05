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

package com.google.code.guice.repository.testing.junit.general.threading;

import com.google.code.guice.repository.testing.junit.RepoTestBase;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class UniqueEntityManagerPerThreadTest extends RepoTestBase {

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testUniqueEntityManagers() throws InterruptedException {
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();
        final Set<Integer> entityManagerHashes = new ConcurrentSkipListSet<Integer>();

        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            callables.add(Executors.callable(new Runnable() {
                @Override
                public void run() {
                    EntityManager em1 = injector.getInstance(EntityManager.class);
                    EntityManager em2 = injector.getInstance(EntityManager.class);
                    int hashCode1 = em1.hashCode();
                    int hashCode2 = em2.hashCode();
                    logger.info("Thread: " + Thread.currentThread().getName() + ": hc: " + hashCode1);
                    Assert.assertEquals("Different EM's per one thread!", hashCode1, hashCode2);
                    if (entityManagerHashes.isEmpty()) {
                        entityManagerHashes.add(hashCode1);
                    } else if (!entityManagerHashes.add(hashCode1)) {
                        logger.info("Error thread: " + Thread.currentThread().getName());
                        Assert.fail("Produced EntityManager is not unique: " + hashCode1);
                    }
                }
            }));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        List<Future<Object>> futures = executorService.invokeAll(callables);
        for (Future<Object> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
    }
}
