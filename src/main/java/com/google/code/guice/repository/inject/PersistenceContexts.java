/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.inject;

import javax.persistence.PersistenceContext;

/**
 * PersistenceContexts - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 04.12.12
 */
public class PersistenceContexts {

    /*===========================================[ CONSTRUCTORS ]=================*/

    private PersistenceContexts() {
    }

    /*===========================================[ CLASS METHODS ]================*/

    public static PersistenceContext persistenceContext(String unitName) {
        return new PersistenceContextImpl(unitName);
    }
}