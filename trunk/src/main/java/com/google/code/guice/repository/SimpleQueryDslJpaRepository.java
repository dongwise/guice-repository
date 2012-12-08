/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * GuicedQueryDslJpaRepository - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 09.12.12
 */
public class SimpleQueryDslJpaRepository<T, ID extends Serializable> extends QueryDslJpaRepository<T,ID> {

    /*===========================================[ CONSTRUCTORS ]=================*/

    @Inject
    public SimpleQueryDslJpaRepository(@Assisted JpaEntityInformation entityInformation, @Assisted EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
}
