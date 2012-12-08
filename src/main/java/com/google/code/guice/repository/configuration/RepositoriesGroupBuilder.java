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

    /*===========================================[ STATIC VARIABLES ]=============*/



    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Collection<String> repositoriesPackages;
    private String inclusionPattern;
    private String exclusionPattern;
    private Predicate<Class> inclusionPredicate;
    private Predicate<Class> exclusionPredicate;

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

    public RepositoriesGroupBuilder withInclusionPattern(String inclusionPattern) {
        this.inclusionPattern = inclusionPattern;
        return this;
    }

    public RepositoriesGroupBuilder withExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
        return this;
    }

    public RepositoriesGroupBuilder withInclusionFilterPredicate(Predicate<Class> inclusionPredicate) {
        this.inclusionPredicate = inclusionPredicate;
        return this;
    }

    public RepositoriesGroupBuilder withExclusionFilter(Predicate<Class> exclusionPredicate) {
        this.exclusionPredicate = exclusionPredicate;
        return this;
    }

    public static RepositoriesGroupBuilder forPackage(String packageName) {
        return forPackages(Arrays.asList(packageName));
    }

    public static RepositoriesGroupBuilder forPackages(Collection<String> repositoriesPackages) {
        return new RepositoriesGroupBuilder(repositoriesPackages);
    }

    public RepositoriesGroup build() {
        RepositoriesGroup repositoriesGroup = new RepositoriesGroup(repositoriesPackages, persistenceUnitName);
        repositoriesGroup.setIncusionPattern(inclusionPattern);
        repositoriesGroup.setExclusionPattern(exclusionPattern);
        repositoriesGroup.setInclusionFilterPredicate(inclusionPredicate);
        repositoriesGroup.setExclusionFilterPredicate(exclusionPredicate);
        return repositoriesGroup;
    }
}