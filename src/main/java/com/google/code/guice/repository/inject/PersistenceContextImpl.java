/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.inject;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * PersistenceContextImpl - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 04.12.12
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class PersistenceContextImpl implements PersistenceContext, Serializable {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final long serialVersionUID = -1340795385726480910L;
    private static final PersistenceProperty[] PERSISTENCE_PROPERTIES = new PersistenceProperty[0];

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private final String unitName;

    /*===========================================[ CONSTRUCTORS ]=================*/

    PersistenceContextImpl(String unitName) {
        this.unitName = unitName;
    }

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public String name() {
        return unitName;
    }

    @Override
    public String unitName() {
        return unitName;
    }

    @Override
    public PersistenceContextType type() {
        return PersistenceContextType.EXTENDED;
    }

    @Override
    public PersistenceProperty[] properties() {
        return PERSISTENCE_PROPERTIES;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
