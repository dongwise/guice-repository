/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.inject;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;

/**
 * TransactionalImpl - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 05.12.12
 */
class TransactionalImpl implements Transactional {
    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private final String value;

    /*===========================================[ CONSTRUCTORS ]=================*/

    TransactionalImpl() {
        value = null;
    }

    TransactionalImpl(String value) {
        this.value = value;
    }
    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public String value() {
        return value;
    }

    @Override
    public Propagation propagation() {
        return null;
    }

    @Override
    public Isolation isolation() {
        return null;
    }

    @Override
    public int timeout() {
        return 0;
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public Class<? extends Throwable>[] rollbackFor() {
        return null;
    }

    @Override
    public String[] rollbackForClassName() {
        return new String[0];
    }

    @Override
    public Class<? extends Throwable>[] noRollbackFor() {
        return null;
    }

    @Override
    public String[] noRollbackForClassName() {
        return new String[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Transactional.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionalImpl)) {
            return false;
        }

        TransactionalImpl transactional = (TransactionalImpl) o;

        if (value != null ? !value.equals(transactional.value) : transactional.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TransactionalImpl");
        sb.append("{value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
