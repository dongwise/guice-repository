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

package com.google.code.guice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

/**
 * Repository with batch save support. Some theory can be found <a href="http://www.objectdb.com/java/jpa/persistence/store#Batch_Store_">here</a>.
 *
 * @param <T> entity type.
 *
 * @author Alexey Krylov
 * @since 10.04.2012
 */
public interface BatchStoreJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /*===========================================[ INTERFACE METHODS ]==============*/

    /**
     * Saves all given entities in one batch. This method will detach saved enities from PersistentContext (L1 cache)
     * to
     * prevent OutOfMemoryException.
     *
     * @param entities entities to save. Should be not null and not empty.
     */
    void saveInBatch(Iterable<T> entities);

    /**
     * Saves given entities in N batch iterations. Each batch contains {@code batchSize} elements. This method will
     * detach saved enities from PersistentContext (L1 cache) to prevent OutOfMemoryException.
     *
     * @param entities  entities to save. Should be not null and not empty.
     * @param batchSize count of entities to save per one batch. Should be positive number.
     */
    void saveInBatch(Iterable<T> entities, int batchSize);
}
