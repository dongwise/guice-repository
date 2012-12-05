/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.inject;

import org.springframework.transaction.annotation.Transactional;

/**
 * Transactions - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 04.12.12
 */
public class Transactions {

    /*===========================================[ CONSTRUCTORS ]=================*/

    private Transactions() {
    }

    /*===========================================[ CLASS METHODS ]================*/

    public static Transactional transactional(String unitName) {
        return new TransactionalImpl(unitName);
    }


    public static Transactional defaultTransactional() {
        return new TransactionalImpl();
    }
}