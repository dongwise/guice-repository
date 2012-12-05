/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.testing.junit.multi;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;

/**
 * MultiUnitsService - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 04.12.12
 */
public class MultiUnitsService {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]===========*/

/*===========================================[ CONSTRUCTORS ]=================*/
/*===========================================[ CLASS METHODS ]================*/
    @Transactional
    public void primaryTransaction(){

    }

    @Transactional("test-h2-secondary")
    public void secondaryTransaction(){

    }
}
