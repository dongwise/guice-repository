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

package com.google.code.guice.repository;

import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class ThreadLocalEntityManagerProvider implements Provider<EntityManager> {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalEntityManagerProvider.class);

    private final ThreadLocal<EntityManager> entityManager;
    private AtomicLong count;
    private final EntityManagerFactory emFactory;

    public ThreadLocalEntityManagerProvider(EntityManagerFactory emFactory) {
        this.emFactory = emFactory;
        entityManager = new ThreadLocal<EntityManager>();
        count = new AtomicLong();
    }

    @Override
    public EntityManager get() {
        count.incrementAndGet();
        if (entityManager.get() == null || !entityManager.get().isOpen()) {
            synchronized (entityManager) {
                entityManager.remove();
                entityManager.set(emFactory.createEntityManager());
            }
        }
//        EntityManager entityManager1 = entityManager.get();
//        System.out.println(String.format("return: [%s] -> [%s], count: [%d], this: [%s]", Thread.currentThread().getName(), entityManager1, count.get(), this));
        return entityManager.get();
    }
}
