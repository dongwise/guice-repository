/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.spi;

import com.google.code.guice.repository.SimpleQueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;

/**
 * SimpleQueryDslJpaRepositoryFactory - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 09.12.12
 */
public interface SimpleQueryDslJpaRepositoryFactory {

    /*===========================================[ INTERFACE METHODS ]==============*/

    SimpleQueryDslJpaRepository create(JpaEntityInformation jpaEntityInformation, EntityManager entityManager);
}
