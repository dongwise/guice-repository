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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SimpleBatchStoreJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        BatchStoreRepository<T>, LowLevelJpaRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleBatchStoreJpaRepository.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager entityManager;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public SimpleBatchStoreJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public SimpleBatchStoreJpaRepository(JpaEntityInformation<T, ID> entityMetadata, EntityManager entityManager) {
        super(entityMetadata, entityManager);
        this.entityManager = entityManager;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public void saveInBatch(Iterable<T> entities) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);
        List<T> saved = save(list);
        cleanup(saved);
    }

    @Transactional(readOnly = true)
    protected void cleanup(List<T> saved) {
        for (T t : saved) {
            entityManager.detach(t);
        }
    }

    public void saveInBatch(Iterable<T> entities, int batchSize) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);

        String entityClassName = list.iterator().next().getClass().getSimpleName();
        logger.info(String.format("batch for [%s] of [%d]", entityClassName, list.size()));

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
                    T saved = save(entity);
                    if (saved != null) {
                        entityManager.detach(entity);
                    }
                }
            } finally {
                startIndex += batchSize;
            }
        }

        logger.info(String.format("batch for [%s] of [%d] stored", entityClassName, list.size()));
    }

    private T doSave(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
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
        return save(entities);
        /*EntityTransaction transaction = entityManager.getTransaction();
        List<T> saved = new ArrayList<T>(0);
        try {
            transaction.begin();
            saved = save(entities);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error", e);
            transaction.rollback();
        }

        return saved;*/
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}