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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleBatchStoreJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        BatchStoreRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleBatchStoreJpaRepository.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager em;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public SimpleBatchStoreJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        em = entityManager;
    }

    public SimpleBatchStoreJpaRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public void saveInBatch(Iterable<T> entities) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);

        List<T> saved = doSave(list);
        for (T t : saved) {
            em.detach(t);
        }
    }

    public void saveInBatch(Iterable<T> entities, int batchSize) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);

        String entityClassName = list.iterator().next().getClass().getSimpleName();
        logger.debug(String.format("batch of [%d]: [%s]", list.size(), entityClassName));

        int startIndex = 0;
        int count = list.size();
        while (startIndex < count) {
            int endIndex = startIndex + batchSize;

            if (endIndex > count) {
                endIndex = count;
            }

            List<T> batch = list.subList(startIndex, endIndex);
            try {
                logger.info(String.format("Storing elements: [%d - %d]", startIndex, endIndex));
                saveInBatch(batch);
                logger.info(String.format("[%d - %d] stored", startIndex, endIndex));
            } catch (Exception e) {
                logger.error(String.format("Error while storing [%d - %d] of [%s], trying single store...", startIndex, endIndex, entityClassName), e);
                for (T entity : batch) {
                    T saved = doSave(entity);
                    if (saved != null) {
                        em.detach(entity);
                    }
                }
            } finally {
                startIndex += batchSize;
            }
        }

        logger.info(String.format("batch of [%d]: [%s] stored", list.size(), entityClassName));
    }

    private T doSave(T entity) {
        EntityTransaction transaction = em.getTransaction();
        T saved = null;
        try {
            transaction.begin();
            saved = save(entity);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error", e);
            transaction.rollback();
        }

        return saved;
    }


    private List<T> doSave(List<T> entities) {
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