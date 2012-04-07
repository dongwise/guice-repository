/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 07.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.io.Serializable;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;

/**
 * Spring-data specifics - we need this because our "base" Repository implementation is not a SimpleJpaRepository, but
 * Repository with batch-store support.
 * @see SimpleBatchStoreJpaRepository
 */
public class CustomJpaRepositoryFactory extends JpaRepositoryFactory {
    CustomJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected JpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainClass());

        if (isQueryDslExecutor(repositoryInterface)) {
            return new QueryDslJpaRepository(entityInformation, entityManager);
        } else {
            return new SimpleBatchStoreJpaRepository(entityInformation, entityManager);
        }
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
            return QueryDslJpaRepository.class;
        } else {
            return SimpleBatchStoreJpaRepository.class;
        }
    }

    @SuppressWarnings({"MethodOverridesPrivateMethodOfSuperclass"})
    private boolean isQueryDslExecutor(Class<?> repositoryInterface) {
        return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
    }
}
