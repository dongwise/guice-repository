/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.configuration;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * RepositoriesGroupFilterPredicates - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public interface RepositoriesGroupFilterPredicates {

    Predicate<Class> AlowAll = new Predicate<Class>() {
        @Override
        public boolean apply(@Nullable Class input) {
            return true;
        }
    };

    Predicate<Class> DenyAll = new Predicate<Class>() {
        @Override
        public boolean apply(@Nullable Class input) {
            return false;
        }
    };
}
