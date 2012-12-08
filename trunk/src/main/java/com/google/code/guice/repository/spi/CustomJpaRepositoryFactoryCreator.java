/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.spi;

import javax.persistence.EntityManager;

/**
 * CustomeJpaRepositoryFactoryCreator - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 09.12.12
 */
public interface CustomJpaRepositoryFactoryCreator {

    /*===========================================[ INTERFACE METHODS ]==============*/

    CustomJpaRepositoryFactory create(EntityManager entityManager);
}
