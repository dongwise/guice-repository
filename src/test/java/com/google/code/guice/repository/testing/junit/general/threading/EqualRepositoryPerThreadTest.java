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

package com.google.code.guice.repository.testing.junit.general.threading;

import com.google.code.guice.repository.testing.junit.RepoTestBase;
import com.google.code.guice.repository.testing.repo.UserRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class EqualRepositoryPerThreadTest extends RepoTestBase {

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testRepositoryPerThreadEquality() throws InterruptedException {
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();
        final Set<Integer> repositoryHashes = new ConcurrentSkipListSet<Integer>();

        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            callables.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    UserRepository repo1 = injector.getInstance(UserRepository.class);
                    UserRepository repo2 = injector.getInstance(UserRepository.class);
                    int hashCode1 = repo1.hashCode();
                    int hashCode2 = repo2.hashCode();
                    Assert.assertEquals("Not equal Repo's per one thread!", hashCode1, hashCode2);

                    if (repositoryHashes.isEmpty()) {
                        repositoryHashes.add(hashCode1);
                    } else if (repositoryHashes.add(hashCode1)) {
                        Assert.fail("Produced Repo is new: " + hashCode1);
                    }
                    return null;
                }
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        List<Future<Object>> futures = executorService.invokeAll(callables);
        for (Future<Object> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                logger.error("Error", e);
                Assert.fail(e.getMessage());
            }
        }
    }
}
