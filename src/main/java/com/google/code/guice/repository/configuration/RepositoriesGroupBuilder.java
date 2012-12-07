/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.configuration;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * RepositoryGroupBuilder - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 08.12.12
 */
public class RepositoriesGroupBuilder {
    private String persistenceUnitName;
    private Predicate<String> exclusionsFilter;
    private Collection<String> repositoriesPackages;
    private Predicate<String> inclusionsFilter;
    private String exclusionsPattern;
    private String inclusionsPattern;
    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]===========*/
/*===========================================[ CONSTRUCTORS ]=================*/
    protected RepositoriesGroupBuilder(Collection<String> repositoriesPackages) {
        this.repositoriesPackages = new ArrayList<String>(repositoriesPackages);
    }

/*===========================================[ CLASS METHODS ]================*/

    public RepositoriesGroupBuilder withRepositoriesPackages(Collection<String> repositoriesPackages) {
        this.repositoriesPackages.addAll(repositoriesPackages);
        return this;
    }

    public RepositoriesGroupBuilder attachedTo(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        return this;
    }

    //TODO class??
    public RepositoriesGroupBuilder withExclusionsFilter(Predicate<String> exclusionsFilter) {
        this.exclusionsFilter = exclusionsFilter;
        return this;
    }

    public RepositoriesGroupBuilder withInclusionsFilter(Predicate<String> inclusionsFilter) {
        this.inclusionsFilter = inclusionsFilter;
        return this;
    }

    public RepositoriesGroupBuilder withExclusionsPattern(String exclusionsPattern) {
        this.exclusionsPattern = exclusionsPattern;
        return this;
    }

    public RepositoriesGroupBuilder withInclusionsPattern(String inclusionsPattern) {
        this.inclusionsPattern = inclusionsPattern;
        return this;
    }

    public static RepositoriesGroupBuilder forPackage(String packageName) {
        return forPackages(Arrays.asList(packageName));
    }

    public static RepositoriesGroupBuilder forPackages(Collection<String> repositoriesPackages) {
        return new RepositoriesGroupBuilder(repositoriesPackages);
    }

    public RepositoriesGroup build() {
        return new RepositoriesGroup(repositoriesPackages, persistenceUnitName);
    }
}
