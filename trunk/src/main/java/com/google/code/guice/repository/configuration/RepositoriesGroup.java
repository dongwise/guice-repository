/*
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.guice.repository.configuration;

import com.google.common.base.Predicate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * RepositoryGroup - TODO: description
 *
 * @author Alexey Krylov (lexx)
 * @since 07.12.12
 */
public class RepositoriesGroup {


    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Collection<String> repositoriesPackages;
    private Pattern inclusionPattern;
    private Pattern exclusionPattern;
    private Predicate<Class> inclusionPredicate;
    private Predicate<Class> exclusionPredicate;

    /*===========================================[ CLASS METHODS ]================*/

    protected RepositoriesGroup(String repositoriesPackage, String persistenceUnitName) {
        this(Arrays.asList(repositoriesPackage), persistenceUnitName);
    }

    protected RepositoriesGroup(Collection<String> repositoriesPackages, String persistenceUnitName) {
        this.repositoriesPackages = new ArrayList<String>(repositoriesPackages);
        this.persistenceUnitName = persistenceUnitName;
    }

    /*===========================================[ CONSTRUCTORS ]=================*/

    public Collection<String> getRepositoriesPackages() {
        return Collections.unmodifiableCollection(repositoriesPackages);
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    protected void setIncusionPattern(String inclusionPattern) {
        if (inclusionPattern != null && !inclusionPattern.isEmpty()) {
            this.inclusionPattern = Pattern.compile(inclusionPattern);
            if (inclusionPredicate == null) {
                // inclusionPattern is a strict parameter - we need to deny other matcher possibilities
                inclusionPredicate = RepositoriesGroupFilterPredicates.DenyAll;
            }
        }
    }

    protected void setExclusionPattern(String exclusionPattern) {
        if (exclusionPattern != null && !exclusionPattern.isEmpty()) {
            this.exclusionPattern = Pattern.compile(exclusionPattern);
        }
    }

    protected void setInclusionFilterPredicate(Predicate<Class> inclusionPredicate) {
        if (inclusionPredicate != null) {
            this.inclusionPredicate = inclusionPredicate;
            if (inclusionPattern == null) {
                // strict parameters set - we need to deny other matcher possibilities
                inclusionPattern = Pattern.compile(RepositoriesGroupPatterns.DenyAll);
            }
        }
    }

    protected void setExclusionFilterPredicate(Predicate<Class> exclusionPredicate) {
        if (exclusionPredicate != null) {
            this.exclusionPredicate = exclusionPredicate;
        }
    }

    public Pattern getInclusionPattern() {
        return inclusionPattern;
    }

    public Pattern getExclusionPattern() {
        return exclusionPattern;
    }

    public Predicate<Class> getInclusionFilterPredicate() {
        return inclusionPredicate;
    }

    public Predicate<Class> getExclusionFilterPredicate() {
        return exclusionPredicate;
    }

    public boolean matches(Class<?> repositoryClass) {
        Assert.notNull(repositoryClass);
        String className = repositoryClass.getName();

        boolean matches = false;
        if (inclusionPatternMatches(className) || inclustionFilterPredicateMatches(repositoryClass)) {
            matches = true;
        }

        if (matches) {
            if (exclusionPatternMatches(className) || exclusionFilterPredicateMatches(repositoryClass)) {
                matches = false;
            }
        }

        return matches;
    }

    private boolean inclusionPatternMatches(String className) {
        return inclusionPattern == null || inclusionPattern.matcher(className).matches();
    }

    private boolean inclustionFilterPredicateMatches(Class<?> repositoryClass) {
        return inclusionPredicate == null || inclusionPredicate.apply(repositoryClass);
    }

    private boolean exclusionPatternMatches(String className) {
        return exclusionPattern != null && exclusionPattern.matcher(className).matches();
    }

    private boolean exclusionFilterPredicateMatches(Class<?> repositoryClass) {
        return exclusionPredicate != null && exclusionPredicate.apply(repositoryClass);
    }
}