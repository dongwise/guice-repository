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

package com.google.code.repository;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * JpaRepository with batch save support. It's a default implementation for all JPA repositories based on
 * guice-repository.
 *
 * @author Alexey Krylov AKA lexx
 * @see SimpleJpaRepository
 */

@Repository
@Transactional(readOnly = true)
public class SimpleBatchStoreJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        BatchStoreJpaRepository<T, ID>, EntityManagerProvider {

    /*==========================================[ STATIC VARIABLES ]=============*/

    private Logger logger;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager entityManager;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public SimpleBatchStoreJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        this(JpaEntityInformationSupport.getMetadata(domainClass, entityManager), entityManager);
    }

    public SimpleBatchStoreJpaRepository(JpaEntityInformation<T, ?> entityMetadata, EntityManager entityManager) {
        super(entityMetadata, entityManager);
        this.entityManager = entityManager;
        logger = LoggerFactory.getLogger(getClass());
    }

    /*===========================================[ CLASS METHODS ]==============*/

    /**
     * @param entities сохраняемые сущности.
     */
    public void saveInBatch(Iterable<T> entities) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);
        List<T> saved = save(list);
        flush();
        for (T t : saved) {
            entityManager.detach(t);
        }
    }

    public void saveInBatch(Iterable<T> entities, int batchSize) {
        List<T> list = Lists.newArrayList(entities);
        Assert.notEmpty(list);
        Assert.isTrue(batchSize > 0);

        String entityClassName = list.iterator().next().getClass().getSimpleName();
        logger.info(String.format("batch for [%d] of [%s]", list.size(), entityClassName ));

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
                    T saved = saveAndFlush(entity);
                    if (saved != null) {
                        entityManager.detach(entity);
                    }
                }
            } finally {
                startIndex += batchSize;
            }
        }

        logger.info(String.format("batch for [%d] of [%s] stored", list.size(), entityClassName));
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}