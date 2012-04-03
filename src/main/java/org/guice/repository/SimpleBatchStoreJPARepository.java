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

package org.guice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleBatchStoreJPARepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        BatchStoreRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleBatchStoreJPARepository.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager em;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public SimpleBatchStoreJPARepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        em = entityManager;
    }

    public SimpleBatchStoreJPARepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public void saveInBatch(Iterable<T> entities) {
        List<T> saved = doSave(entities);
        for (T t : saved) {
            em.detach(t);
        }
    }

    private List<T> doSave(Iterable<T> entities) {
        EntityTransaction transaction = em.getTransaction();
        List<T> saved = new ArrayList<T>(0);
        try {
            transaction.begin();
            saved = save(entities);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error", e);
            transaction.rollback();
        }
        return saved;

    }
}