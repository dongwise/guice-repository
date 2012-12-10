/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.configuration;

/**
 * RepositoryBinding - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 11.12.12
 */
public interface RepositoryBinding {

    /*===========================================[ INTERFACE METHODS ]==============*/

    Class getRepositoryClass();

    Class getCustomRepositoryClass();

    String getPersistenceUnitName();
}
